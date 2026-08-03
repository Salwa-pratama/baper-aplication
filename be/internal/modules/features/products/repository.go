package products

import (
	"baper/internal/models"
	"context"

	"gorm.io/gorm"
)





type Repository interface {
	CreateProduct(ctx context.Context,product *models.Product) error
}



type repository struct {
	db *gorm.DB
}

func NewProductRepository(db *gorm.DB) Repository {
	return &repository{db}
}


func (r *repository) CreateProduct(ctx context.Context, product *models.Product) error {
	return r.db.WithContext(ctx).Create(product).Error
}
