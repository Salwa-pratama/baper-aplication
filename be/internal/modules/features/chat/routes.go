package chat

import (
	"context"
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"google.golang.org/genai"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	ctx := context.Background()

	// Inisialisasi Gemini Client
	client, err := genai.NewClient(ctx, &genai.ClientConfig{
		APIKey: os.Getenv("GEMINI_API_KEY"),
		Backend: genai.BackendGeminiAPI,
	}) // Otomatis membaca env GEMINI_API_KEY
	if err != nil {
		log.Fatal("Gagal inisialisasi Gemini Client: ", err)
	}

	repo := NewChatRepository(db, client)
	svc := NewChatService(repo)
	Ctrl := NewChatController(svc)

	chat := router.Group("/webhook")
	chat.Get("/", Ctrl.CekVerification)
	chat.Post("/verify", Ctrl.ReceiveMessage)
}
