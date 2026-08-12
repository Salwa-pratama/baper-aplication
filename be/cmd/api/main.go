package main

import (
	"baper/internal/config"
	"baper/internal/router"
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"
)

var App *fiber.App

// @title Baper API
// @version 1.0
// @description Backend API for Baper Application
// @host localhost:3000
// @BasePath /
// @securityDefinitions.apikey BearerAuth
// @in header
// @name Authorization
func main() {
	fiberConfig := fiber.Config{
		AppName: "BAPER API",
		// ErrorHandler:          config, // TODO: define or import config
		DisableStartupMessage: true,
	}

	app := fiber.New(fiberConfig)
	db := config.DbSupabase()

	// Recover harus dipasang paling awal: satu panic di handler mana pun
	// tidak boleh mematikan seluruh server.
	app.Use(recover.New(recover.Config{EnableStackTrace: true}))

	app.Use(cors.New(cors.Config{
		AllowOrigins: "*",
		// Authorization wajib ada, kalau tidak klien browser tidak bisa
		// mengirim Bearer token (preflight-nya ditolak).
		AllowHeaders: "Origin, Content-Type, Accept, Authorization",
	}))

	app.Use(logger.New())

	app.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"status":  "ok",
			"message": "Baper API is running",
		})
	})

	router.SetupRoutes(app, db)

	// Listen Server
	port := os.Getenv("APP_PORT")
	// log.Fatal(app.Listen(":" + config.Config.Port)) // TODO: define or import config
	log.Printf("Server running on http://localhost:%s", port)
	log.Fatal(app.Listen(":" + port)) // Fallback port for now

}
