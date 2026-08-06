package busines

import (
	"baper/internal/models"
	"context"
	"errors"
	"sync"

	"gorm.io/gorm"
)

type Repository interface {
	CreateBusiness(ctx context.Context,userID string,busines *models.Business) error
	ExistingBusiness(ctx context.Context,userID string, name string ) (*models.Business, error)
	FindBusinessByUserID(ctx context.Context, userID string) (*models.Business, error)
	AddProduct(ctx context.Context,product *models.Product) error
	ExistingCustomer(ctx context.Context, custId string) error
	GetMonthlyRecap(ctx context.Context, businessID string, startDate, endDate string) (*MonthlyRecapResponse, error)
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

func (r *repository) FindBusinessByUserID(ctx context.Context, userID string) (*models.Business, error) {
	var business models.Business
	err := r.db.WithContext(ctx).Where("user_id = ?", userID).First(&business).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("business tidak ditemukan")
		}
		return nil, err
	}
	return &business, nil
}



// Berguna untuk membuat suatu product
func (r *repository) AddProduct(ctx context.Context,product *models.Product) error {
	return r.db.WithContext(ctx).Create(product).Error
}
func (r *repository) ExistingCustomer(ctx context.Context, custId string) error {
	var customer models.Customer
	return r.db.WithContext(ctx).Where("id = ?", custId).First(&customer).Error
}

func (r *repository) AddCustomer(ctx context.Context,customer *models.Product) error {
	return r.db.WithContext(ctx).Create(customer).Error
}

func (r *repository) GetMonthlyRecap(ctx context.Context, businessID string, startDate, endDate string) (*MonthlyRecapResponse, error) {
	var recap MonthlyRecapResponse

	// Kita gunakan WaitGroup untuk menjalankan query secara paralel (concurrency)
	var wg sync.WaitGroup
	errCh := make(chan error, 4) // Tampungan error jika ada goroutine yang gagal

	wg.Add(4) // Ada 4 query paralel yang mau dieksekusi

	// Query 1: Total Revenue (Status Paid)
	go func() {
		defer wg.Done()
		var revenue float64
		err := r.db.WithContext(ctx).Model(&models.Order{}).
			Where("business_id = ? AND status = ? AND created_at >= ? AND created_at < ?", businessID, models.OrderStatusPaid, startDate, endDate).
			Select("COALESCE(SUM(total_amount), 0)").Scan(&revenue).Error
		if err != nil {
			errCh <- err
			return
		}
		recap.TotalRevenue = revenue
	}()

	// Query 2: Total Orders Keseluruhan
	go func() {
		defer wg.Done()
		var total int64
		err := r.db.WithContext(ctx).Model(&models.Order{}).
			Where("business_id = ? AND created_at >= ? AND created_at < ?", businessID, startDate, endDate).
			Count(&total).Error
		if err != nil {
			errCh <- err
			return
		}
		recap.TotalOrders = int(total)
	}()

	// Query 3: Total Paid Orders
	go func() {
		defer wg.Done()
		var paid int64
		err := r.db.WithContext(ctx).Model(&models.Order{}).
			Where("business_id = ? AND status = ? AND created_at >= ? AND created_at < ?", businessID, models.OrderStatusPaid, startDate, endDate).
			Count(&paid).Error
		if err != nil {
			errCh <- err
			return
		}
		recap.TotalPaidOrders = int(paid)
	}()

	// Query 4: Total Unpaid Orders
	go func() {
		defer wg.Done()
		var unpaid int64
		err := r.db.WithContext(ctx).Model(&models.Order{}).
			Where("business_id = ? AND status = ? AND created_at >= ? AND created_at < ?", businessID, models.OrderStatusUnpaid, startDate, endDate).
			Count(&unpaid).Error
		if err != nil {
			errCh <- err
			return
		}
		recap.TotalUnpaidOrders = int(unpaid)
	}()

	// Tunggu semua query selesai
	wg.Wait()
	close(errCh)

	// Jika ada error dari salah satu goroutine, kembalikan error
	for err := range errCh {
		if err != nil {
			return nil, err
		}
	}

	return &recap, nil
}
