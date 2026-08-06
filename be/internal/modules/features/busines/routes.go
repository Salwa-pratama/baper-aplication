package busines

import (
	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)





func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewBusinesRepository(db)
	svc := NewBusinessService(repo)
	ctrl := NewBusinessController(svc)

	business := router.Group("/business")

	business.Post("/register",middleware.AuthMiddleware(), ctrl.RegisterBusiness)
	business.Get("/recap/monthly", middleware.AuthMiddleware(), ctrl.GetMonthlyRecap)
	business.Get("/recap/monthly/export", middleware.AuthMiddleware(), ctrl.ExportMonthlyRecap)
}
