package conversation

import (
	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewConversationRepository(db)
	svc := NewConversationService(repo)
	ctrl := NewConversationController(svc)

	// Semua endpoint percakapan wajib token: isi chat adalah data privat
	// milik satu bisnis.
	conversations := router.Group("/conversations", middleware.AuthMiddleware())

	// GET /api/conversations                  -> daftar card chat
	// GET /api/conversations/:id/messages     -> isi chat saat card ditekan
	conversations.Get("/", ctrl.ListConversations)
	conversations.Get("/:id/messages", ctrl.GetConversationMessages)
}
