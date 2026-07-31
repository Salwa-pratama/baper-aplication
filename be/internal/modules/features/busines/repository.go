package busines

import (
	"baper/internal/models"
	"context"
	"errors"

	"gorm.io/gorm"
)

type Repository interface {
	CreateBusiness(ctx context.Context,userID string,busines *models.Business) error
	ExistingBusiness(ctx context.Context,userID string, name string ) (*models.Business, error)
	AddProduct(ctx context.Context,product *models.Product) error
}


type repository struct {
	db *gorm.DB
}

func NewBusinesRepository(db *gorm.DB) Repository {
	return &repository{db}
}

// dengan context — query ini "sadar" kalau request-nya udah dibatalin/timeout
func (r *repository) CreateBusiness(ctx context.Context, userID string, business *models.Business) error {
	business.UserID = userID
	return r.db.WithContext(ctx).Create(business).Error
}

func (r *repository) ExistingBusiness(ctx context.Context, userID string, name string) (*models.Business, error) {
	var business models.Business

	err := r.db.WithContext(ctx).
		Where("user_id = ? AND name = ?", userID, name).
		First(&business).Error

	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil // nggak ketemu = bukan error, cuma nil aja
		}
		return nil, err // error beneran (misal DB down)
	}

	return &business, nil
}



// Berguna untuk membuat suatu product
func (r *repository) AddProduct(ctx context.Context,product *models.Product) error {
	return r.db.WithContext(ctx).Create(product).Error
}


func (r *repository) AddCustomer(ctx context.Context,customer *models.Product) error {
	return r.db.WithContext(ctx).Create(customer).Error
}
