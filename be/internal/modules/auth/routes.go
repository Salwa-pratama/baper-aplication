package auth

import (
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewAuthRepository(db) // Mungkin nanti lebih baik namanya NewAuthRepository
	svc := NewAuthService(repo)   // Mungkin nanti lebih baik namanya NewAuthService
	loginCtrl := NewLoginController(svc)
	registerCtrl := NewRegisterController(svc)

	auth := router.Group("/auth")
	auth.Post("/login", loginCtrl.Login)
	auth.Post("/register", registerCtrl.Register)
}
