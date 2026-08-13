package profile

import (
	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewProfileRepository(db)
	svc := NewProfileService(repo)
	ctrl := NewProfileController(svc)

	g := router.Group("/profile", middleware.AuthMiddleware())

	g.Get("/", ctrl.GetProfile)
}
