package chat

import (
	"baper/internal/models"
	"context"
	"errors"
	"fmt"
	"os"

	"google.golang.org/genai"
	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

type Repository interface {
	SaveChatSession(chat_session *models.ChatSession) error
	SaveMessage(message *models.Message) error
	FindBotByBusinessPhone(phone string) (*models.Bot, error)
	FindCustomerByPhone(phone string) (*models.Customer, error)
	FindCustomerByPhoneAndBusiness(phone, businessID string) (*models.Customer, error)
	CreateCustomer(customer *models.Customer) error
	FindActiveChatSession(botID, customerID string) (*models.ChatSession, error)
	GetRecentMessages(sessionID string, limit int) ([]models.Message, error)
	GetProductsByBusinessID(businessID string) ([]models.Product, error)
	// SaveOrder menyimpan order + item DAN mengurangi stok dalam satu
	// transaksi. Stok dikunci (SELECT ... FOR UPDATE) supaya dua pesanan
	// bersamaan tidak bisa membuat stok jadi minus.
	SaveOrder(order *models.Order, items []models.OrderItem) error
	GenerateContent(botPrompt string, historyMsgs []models.Message, productCatalog string, prompt string) (string, error)
}

type repository struct {
	db     *gorm.DB
	client *genai.Client
}

func NewChatRepository(db *gorm.DB, client *genai.Client) Repository {
	return &repository{db, client}
}

func (r *repository) SaveChatSession(chat_session *models.ChatSession) error {
	return r.db.Create(chat_session).Error
}

func (r *repository) SaveMessage(message *models.Message) error {
	return r.db.Create(message).Error
}

func (r *repository) GenerateContent(botPrompt string, historyMsgs []models.Message, productCatalog string, prompt string) (string, error) {
	ctx := context.Background()

	// Default bot prompt if empty
	if botPrompt == "" {
		fileBytes, err := os.ReadFile("internal/modules/features/chat/promt.txt")
		if err == nil {
			botPrompt = string(fileBytes)
		} else {
			botPrompt = "Kamu adalah asisten e-commerce AI yang ramah."
		}
	}

	// Gabungkan instruksi RAG
	systemInstruction := fmt.Sprintf(`%s

Berikut adalah Katalog Produk yang tersedia (ID, Nama, Harga, Stok):
%s

Jika pembeli ingin memesan dan stok kosong, tolak dengan sopan.
Jika pembeli FIX/DEAL memesan produk, KELUARKAN RESPONS JSON SPESIFIK SAJA di akhir pesanmu dalam block kode JSON dengan format:
{
  "is_order_final": true,
  "items": [{"product_id": "...", "qty": 1}]
}
`, botPrompt, productCatalog)

	config := &genai.GenerateContentConfig{
		SystemInstruction: &genai.Content{
			Parts: []*genai.Part{{Text: systemInstruction}},
		},
	}

	// Build history array
	var contents []*genai.Content
	for _, msg := range historyMsgs {
		role := "user"
		if msg.SenderType == "bot" {
			role = "model" // Gemini SDK expects "model" for AI replies
		}
		contents = append(contents, &genai.Content{
			Role:  role,
			Parts: []*genai.Part{{Text: msg.Content}},
		})
	}

	// Add current prompt
	contents = append(contents, &genai.Content{
		Role:  "user",
		Parts: []*genai.Part{{Text: prompt}},
	})

	resp, err := r.client.Models.GenerateContent(
		ctx,
		os.Getenv("GEMINI_MODEL"),
		contents, // Pass the structured history array directly
		config,
	)

	if err != nil {
		return "", fmt.Errorf("gagal generate content: %w", err)
	}
	return resp.Text(), nil
}

func (r *repository) FindBotByBusinessPhone(phone string) (*models.Bot, error) {
	var bot models.Bot
	// Karena di Supabase datanya diisi di kolom wa_number tabel bots, kita pakai query ini
	err := r.db.Where("wa_number = ?", phone).First(&bot).Error
	if err != nil {
		return nil, err
	}
	return &bot, nil
}

func (r *repository) FindCustomerByPhone(phone string) (*models.Customer, error) {
	var customer models.Customer
	err := r.db.Where("wa_phone_number = ?", phone).First(&customer).Error
	if err != nil {
		return nil, err
	}
	return &customer, nil
}

// FindCustomerByPhoneAndBusiness mencari customer dalam scope satu bisnis.
// Tanpa scope business_id, nomor WhatsApp yang sama pada dua bisnis berbeda
// akan saling tertukar datanya.
func (r *repository) FindCustomerByPhoneAndBusiness(phone, businessID string) (*models.Customer, error) {
	var customer models.Customer
	err := r.db.Where("wa_phone_number = ? AND business_id = ?", phone, businessID).First(&customer).Error
	if err != nil {
		return nil, err
	}
	return &customer, nil
}

func (r *repository) CreateCustomer(customer *models.Customer) error {
	return r.db.Create(customer).Error
}

func (r *repository) FindActiveChatSession(botID, customerID string) (*models.ChatSession, error) {
	var session models.ChatSession
	err := r.db.Where("bot_id = ? AND customer_id = ? AND status = ?", botID, customerID, "active").Order("started_at desc").First(&session).Error
	if err != nil {
		return nil, err
	}
	return &session, nil
}

func (r *repository) GetRecentMessages(sessionID string, limit int) ([]models.Message, error) {
	var msgs []models.Message
	err := r.db.Where("session_id = ?", sessionID).Order("created_at asc").Limit(limit).Find(&msgs).Error
	return msgs, err
}

func (r *repository) GetProductsByBusinessID(businessID string) ([]models.Product, error) {
	var prods []models.Product
	err := r.db.Where("business_id = ?", businessID).Find(&prods).Error
	return prods, err
}

// ErrStokKurang dikembalikan jika stok tidak cukup saat order dibuat.
var ErrStokKurang = errors.New("stok tidak cukup")

// SaveOrder menyimpan order + item DAN mengurangi stok, semuanya dalam satu
// transaksi. Baris produk dikunci dengan SELECT ... FOR UPDATE agar dua
// pesanan yang datang bersamaan tidak bisa menembus stok jadi negatif.
// Kalau ada satu item yang stoknya kurang, SELURUH order dibatalkan (rollback).
func (r *repository) SaveOrder(order *models.Order, items []models.OrderItem) error {
	if len(items) == 0 {
		return errors.New("order tanpa item")
	}

	return r.db.Transaction(func(tx *gorm.DB) error {
		// 1. Kunci + validasi + kurangi stok tiap item lebih dulu.
		for i := range items {
			if items[i].Quantity <= 0 {
				return fmt.Errorf("qty tidak valid untuk produk %s", items[i].ProductID)
			}

			var product models.Product
			if err := tx.Clauses(clause.Locking{Strength: "UPDATE"}).
				Where("id = ?", items[i].ProductID).
				First(&product).Error; err != nil {
				return fmt.Errorf("produk %s tidak ditemukan: %w", items[i].ProductID, err)
			}

			// Produk harus milik bisnis yang sama dengan order.
			if product.BusinessID != order.BusinessID {
				return fmt.Errorf("produk %s bukan milik bisnis ini", items[i].ProductID)
			}

			if product.Stock < items[i].Quantity {
				return fmt.Errorf("%w: produk %s sisa %d, diminta %d",
					ErrStokKurang, product.Name, product.Stock, items[i].Quantity)
			}

			// Harga selalu diambil dari DB, bukan dari input AI/klien.
			items[i].Price = product.Price

			if err := tx.Model(&models.Product{}).
				Where("id = ?", product.ID).
				Update("stock", gorm.Expr("stock - ?", items[i].Quantity)).Error; err != nil {
				return err
			}
		}

		// 2. Hitung ulang total dari harga DB, jangan percaya hitungan AI.
		var total float64
		for i := range items {
			total += items[i].Price * float64(items[i].Quantity)
		}
		order.TotalAmount = total

		// 3. Baru simpan order + item-nya.
		if err := tx.Create(order).Error; err != nil {
			return err
		}
		for i := range items {
			items[i].OrderID = order.ID
			if err := tx.Create(&items[i]).Error; err != nil {
				return err
			}
		}
		return nil
	})
}
