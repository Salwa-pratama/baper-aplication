package orders

import (
	"baper/internal/models"
	"context"

	"gorm.io/gorm"
)

type Repository interface {
	FindBusinessByUserID(ctx context.Context, userID string) (*models.Business, error)
	ListOrdersByBusinessID(ctx context.Context, businessID string) ([]models.Order, error)
	UpdateOrderStatus(ctx context.Context, orderID, businessID string, status models.OrderStatus) error
}

type repository struct {
	db *gorm.DB
}

func NewOrderRepository(db *gorm.DB) Repository {
	return &repository{db}
}

func (r *repository) FindBusinessByUserID(ctx context.Context, userID string) (*models.Business, error) {
	var business models.Business
	if err := r.db.WithContext(ctx).Where("user_id = ?", userID).First(&business).Error; err != nil {
		return nil, err
	}
	return &business, nil
}

func (r *repository) ListOrdersByBusinessID(ctx context.Context, businessID string) ([]models.Order, error) {
	var orders []models.Order
	err := r.db.WithContext(ctx).
		Preload("Customer").
		Preload("OrderItems").
		Preload("OrderItems.Product").
		Where("business_id = ?", businessID).
		Order("created_at DESC").
		Find(&orders).Error
	return orders, err
}

func (r *repository) UpdateOrderStatus(ctx context.Context, orderID, businessID string, status models.OrderStatus) error {
	return r.db.WithContext(ctx).
		Model(&models.Order{}).
		Where("id = ? AND business_id = ?", orderID, businessID).
		Update("status", status).Error
}
