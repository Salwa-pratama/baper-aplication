package orders

import (
	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewOrderRepository(db)
	svc := NewOrderService(repo)
	ctrl := NewOrderController(svc)

	orders := router.Group("/orders", middleware.AuthMiddleware())

	orders.Get("/", ctrl.GetOrders)
	orders.Patch("/:id/status", ctrl.ConfirmPayment)
}
