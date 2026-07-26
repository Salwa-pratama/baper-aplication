package main

import (
	"baper/config"
	"baper/internal/router"
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/logger"
)

var App *fiber.App

func main() {
	fiberConfig := fiber.Config{
		AppName: "BAPER API",
		// ErrorHandler:          config, // TODO: define or import config
		DisableStartupMessage: true,
	}

	app := fiber.New(fiberConfig)
	db := config.DbSupabase()

	app.Use(cors.New(cors.Config{
		AllowOrigins: "*",
		AllowHeaders: "Origin, Content-Type, Accept",
	}))

	app.Use(logger.New())



	app.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"status" : "ok",
			"message": "Baper API is running",
		})
	})

	router.SetupRoutes(app, db)

	// Listen Server
	port := os.Getenv("APP_PORT")
	// log.Fatal(app.Listen(":" + config.Config.Port)) // TODO: define or import config
	log.Printf("Server running on http://localhost:%s", port)
	log.Fatal(app.Listen(":"+ port)) // Fallback port for now

}
