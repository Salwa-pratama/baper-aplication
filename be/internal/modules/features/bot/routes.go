package bot

import (
	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(app fiber.Router, db *gorm.DB) {
	repo := NewBotRepository(db)
	service := NewBotService(repo)
	controller := NewBotController(service)

	// Semua endpoint bot wajib token — sebelumnya terbuka untuk publik.
	botRoutes := app.Group("/bots", middleware.AuthMiddleware())
	botRoutes.Patch("/:id/toggle", controller.ToggleBotStatus)
	botRoutes.Put("/:id/prompt", controller.UpdateBotPrompt)
}
