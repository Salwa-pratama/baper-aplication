package chat

import (
	"baper/internal/models"
	"context"
	"fmt"
	"os"

	"google.golang.org/genai"
	"gorm.io/gorm"
)

type Repository interface {
	SaveChatSession(chat_session *models.ChatSession) error
	SaveMessage(message *models.Message) error
	FindBotByBusinessPhone(phone string) (*models.Bot, error)
	FindCustomerByPhone(phone string) (*models.Customer, error)
	CreateCustomer(customer *models.Customer) error
	FindActiveChatSession(botID, customerID string) (*models.ChatSession, error)
	GetRecentMessages(sessionID string, limit int) ([]models.Message, error)
	GetProductsByBusinessID(businessID string) ([]models.Product, error)
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

func (r *repository) SaveOrder(order *models.Order, items []models.OrderItem) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
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
