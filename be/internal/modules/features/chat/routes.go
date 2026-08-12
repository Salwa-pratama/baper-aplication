package chat

import (
	"context"
	"log"
	"os"

	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"google.golang.org/genai"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	ctx := context.Background()

	// Inisialisasi Gemini Client
	client, err := genai.NewClient(ctx, &genai.ClientConfig{
		APIKey:  os.Getenv("GEMINI_API_KEY"),
		Backend: genai.BackendGeminiAPI,
	})
	if err != nil {
		log.Fatal("Gagal inisialisasi Gemini Client: ", err)
	}

	repo := NewChatRepository(db, client)
	svc := NewChatService(repo)
	Ctrl := NewChatController(svc)

	chat := router.Group("/webhook")

	// GET /webhook = handshake verifikasi Meta, dilindungi oleh VERIFY_TOKEN
	// yang dicek di service (bukan Bearer token milik user aplikasi).
	chat.Get("/", Ctrl.CekVerification)

	// POST /webhook/verify = pesan masuk dari Meta.
	// Terbuka tanpa autentikasi: Meta tidak punya JWT kita.
	chat.Post("/verify", Ctrl.ReceiveMessage)

	// Endpoint kirim pesan/media dipakai oleh aplikasi Android → wajib JWT.
	// Sebelumnya terbuka: siapa pun bisa memakai ACCESS_TOKEN WhatsApp kita.
	chat.Post("/send-message", middleware.AuthMiddleware(), Ctrl.SendMessage)
	chat.Post("/send-media", middleware.AuthMiddleware(), Ctrl.SendMediaMessage)
	chat.Post("/upload-media", middleware.AuthMiddleware(), Ctrl.SendMediaFile)
}
