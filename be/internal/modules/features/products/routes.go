package products

import (
	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewProductRepository(db)
	svc := NewProductService(repo)
	ctrl := NewProductController(svc)

	// Semua endpoint produk wajib token — sebelumnya terbuka untuk publik.
	products := router.Group("/products", middleware.AuthMiddleware())

	products.Post("/", ctrl.CreateProduct)
	products.Get("/", ctrl.GetAllProducts)
	products.Get("/:id", ctrl.GetProductByID)
	products.Put("/:id", ctrl.UpdateProduct)
	products.Delete("/:id", ctrl.DeleteProduct)
}
