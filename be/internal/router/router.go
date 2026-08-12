package router

import (
	_ "baper/docs"
	"baper/internal/config"
	login "baper/internal/modules/auth"
	"baper/internal/modules/features/bot"
	"baper/internal/modules/features/busines"
	"baper/internal/modules/features/chat"
	"baper/internal/modules/features/conversation"
	"baper/internal/modules/features/products"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func SetupRoutes(app *fiber.App, db *gorm.DB) {
	// Catatan: logger.New() sudah dipasang sekali di cmd/api/main.go.
	// Sebelumnya dipasang dua kali sehingga setiap request tercatat dobel.
	app.Get("/swagger/*", config.SetupSwagger())

	api := app.Group("/api")

	login.InitRoutes(api, db)
	chat.InitRoutes(api, db)
	busines.InitRoutes(api, db)
	products.InitRoutes(api, db)
	bot.InitRoutes(api, db)
	conversation.InitRoutes(api, db)
}
