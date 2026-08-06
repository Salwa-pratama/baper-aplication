package bot

import (
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(app fiber.Router, db *gorm.DB) {
	repo := NewBotRepository(db)
	service := NewBotService(repo)
	controller := NewBotController(service)

	botRoutes := app.Group("/bots")
	botRoutes.Patch("/:id/toggle", controller.ToggleBotStatus)
	botRoutes.Put("/:id/prompt", controller.UpdateBotPrompt)
}
