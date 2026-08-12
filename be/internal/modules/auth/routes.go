package auth

import (
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewAuthRepository(db)
	svc := NewAuthService(repo)
	loginCtrl := NewLoginController(svc)
	registerCtrl := NewRegisterController(svc)
	refreshCtrl := NewRefreshController(svc)

	auth := router.Group("/auth")
	auth.Post("/login", loginCtrl.Login)
	auth.Post("/register", registerCtrl.Register)
	// Refresh tidak pakai AuthMiddleware: access token-nya justru sudah
	// kedaluwarsa. Yang diverifikasi adalah refresh token di dalam body.
	auth.Post("/refresh", refreshCtrl.RefreshToken)
}
