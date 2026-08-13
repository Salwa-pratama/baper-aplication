package profile

import (
	"baper/internal/models"
	"context"

	"gorm.io/gorm"
)

type Repository interface {
	GetUserProfile(ctx context.Context, userID string) (*models.User, *models.Business, error)
	GetTotalOrders(ctx context.Context, businessID string) (int, error)
	GetTotalCustomers(ctx context.Context, businessID string) (int, error)
}

type repository struct {
	db *gorm.DB
}

func NewProfileRepository(db *gorm.DB) Repository {
	return &repository{db}
}

func (r *repository) GetUserProfile(ctx context.Context, userID string) (*models.User, *models.Business, error) {
	var user models.User
	if err := r.db.WithContext(ctx).Where("id = ?", userID).First(&user).Error; err != nil {
		return nil, nil, err
	}

	var business models.Business
	if err := r.db.WithContext(ctx).Where("user_id = ?", userID).First(&business).Error; err != nil {
		// Business might not exist for some users, but typically does in this app
		if err == gorm.ErrRecordNotFound {
			return &user, nil, nil
		}
		return nil, nil, err
	}

	return &user, &business, nil
}

func (r *repository) GetTotalOrders(ctx context.Context, businessID string) (int, error) {
	var count int64
	if err := r.db.WithContext(ctx).Model(&models.Order{}).Where("business_id = ?", businessID).Count(&count).Error; err != nil {
		return 0, err
	}
	return int(count), nil
}

func (r *repository) GetTotalCustomers(ctx context.Context, businessID string) (int, error) {
	var count int64
	if err := r.db.WithContext(ctx).Model(&models.Customer{}).Where("business_id = ?", businessID).Count(&count).Error; err != nil {
		return 0, err
	}
	return int(count), nil
}
