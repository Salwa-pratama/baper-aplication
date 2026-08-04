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
	GenerateContent(promt string) (string, error)
	FindBotByBusinessPhone(phone string) (*models.Bot, error)
	FindCustomerByPhone(phone string) (*models.Customer, error)
	CreateCustomer(customer *models.Customer) error
	FindActiveChatSession(botID, customerID string) (*models.ChatSession, error)
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

func (r *repository) GenerateContent(prompt string) (string, error) {
	ctx := context.Background()

	fileBytes, err := os.ReadFile("internal/modules/features/chat/promt.txt")
	if err != nil {
		return "", fmt.Errorf("Gagal membaca file promt: %w", err)

	}
	characterAI := string(fileBytes)

	config := &genai.GenerateContentConfig{
		SystemInstruction: &genai.Content{
			Parts: []*genai.Part{{Text: characterAI}},
		},
	}

	resp, err := r.client.Models.GenerateContent(
		ctx,
		os.Getenv("GEMINI_MODEL"),
		genai.Text(prompt),
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
