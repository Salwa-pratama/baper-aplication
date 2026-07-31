package router

import (
	login "baper/internal/modules/auth"
	"baper/internal/modules/features/busines"
	"baper/internal/modules/features/chat"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"gorm.io/gorm"
)




func SetupRoutes(app *fiber.App, db *gorm.DB) {
	app.Use(logger.New())

	api := app.Group("/api")

	login.InitRoutes(api, db)
	chat.InitRoutes(api,db)
	busines.InitRoutes(api, db)
}
