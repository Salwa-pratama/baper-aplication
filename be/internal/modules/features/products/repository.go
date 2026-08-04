package products

import (
	"baper/internal/models"
	"context"

	"gorm.io/gorm"
)

type Repository interface {
	CreateProduct(ctx context.Context, product *models.Product) error
	FindAllProducts(ctx context.Context, businessID string) ([]models.Product, error)
	FindProductByID(ctx context.Context, id string) (*models.Product, error)
	UpdateProduct(ctx context.Context, product *models.Product) error
	DeleteProduct(ctx context.Context, id string) error
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

func (r *repository) FindAllProducts(ctx context.Context, businessID string) ([]models.Product, error) {
	var products []models.Product
	query := r.db.WithContext(ctx)
	
	if businessID != "" {
		query = query.Where("business_id = ?", businessID)
	}

	err := query.Find(&products).Error
	return products, err
}

func (r *repository) FindProductByID(ctx context.Context, id string) (*models.Product, error) {
	var product models.Product
	err := r.db.WithContext(ctx).Where("id = ?", id).First(&product).Error
	if err != nil {
		return nil, err
	}
	return &product, nil
}

func (r *repository) UpdateProduct(ctx context.Context, product *models.Product) error {
	return r.db.WithContext(ctx).Save(product).Error
}

func (r *repository) DeleteProduct(ctx context.Context, id string) error {
	return r.db.WithContext(ctx).Where("id = ?", id).Delete(&models.Product{}).Error
}
