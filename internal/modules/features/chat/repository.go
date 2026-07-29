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
	GenerateContent(promt string) (string, error)
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
