package orders

import (
	"baper/internal/common/apperror"
	"baper/internal/models"
	"context"
	"errors"
	"fmt"
	"time"

	"gorm.io/gorm"
)

type Service interface {
	GetOrders(ctx context.Context, userID string) ([]OrderResponse, error)
	ConfirmPayment(ctx context.Context, userID, orderID string) error
}

type service struct {
	repo Repository
}

func NewOrderService(repo Repository) Service {
	return &service{repo}
}

// Indonesian month names for formatting
var indonesianMonths = []string{
	"", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
	"Juli", "Agustus", "September", "Oktober", "November", "Desember",
}

func formatDate(t time.Time) string {
	day := t.Day()
	month := indonesianMonths[t.Month()]
	year := t.Year()
	return fmt.Sprintf("%d %s %d", day, month, year)
}

func formatCurrency(amount float64) string {
	// Format to "Rp 150.000"
	// Sederhananya tanpa decimal:
	return fmt.Sprintf("Rp %s", formatNumber(int64(amount)))
}

func formatNumber(n int64) string {
	if n < 0 {
		return "-" + formatNumber(-n)
	}
	if n < 1000 {
		return fmt.Sprintf("%d", n)
	}
	return fmt.Sprintf("%s.%03d", formatNumber(n/1000), n%1000)
}

func formatStatus(s models.OrderStatus) string {
	if s == models.OrderStatusPaid {
		return "Sudah Lunas"
	}
	return "Belum Bayar"
}

func (s *service) businessIDOf(ctx context.Context, userID string) (string, error) {
	if userID == "" {
		return "", apperror.Unauthorized("User tidak dikenali")
	}

	business, err := s.repo.FindBusinessByUserID(ctx, userID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return "", apperror.NotFound("Bisnis tidak ditemukan untuk user ini")
		}
		return "", apperror.Internal("Gagal mengambil data bisnis")
	}
	return business.ID, nil
}

func (s *service) GetOrders(ctx context.Context, userID string) ([]OrderResponse, error) {
	businessID, err := s.businessIDOf(ctx, userID)
	if err != nil {
		return nil, err
	}

	orders, err := s.repo.ListOrdersByBusinessID(ctx, businessID)
	if err != nil {
		return nil, apperror.Internal("Gagal mengambil daftar pesanan")
	}

	var results []OrderResponse
	for _, o := range orders {
		packageName := "Tidak ada paket"
		if len(o.OrderItems) > 0 {
			firstItem := o.OrderItems[0]
			packageName = fmt.Sprintf("%s x%d", firstItem.Product.Name, firstItem.Quantity)
			if len(o.OrderItems) > 1 {
				packageName += fmt.Sprintf(" + %d produk lain", len(o.OrderItems)-1)
			}
		}

		sessionID := ""
		if o.SessionID != nil {
			sessionID = *o.SessionID
		}

		customerName := o.Customer.Name
		if customerName == "" {
			customerName = "Tanpa Nama"
		}

		results = append(results, OrderResponse{
			ID:           o.ID,
			CustomerName: customerName,
			PackageName:  packageName,
			Date:         formatDate(o.CreatedAt),
			Amount:       formatCurrency(o.TotalAmount),
			Status:       formatStatus(o.Status),
			ChatID:       sessionID,
		})
	}

	return results, nil
}

func (s *service) ConfirmPayment(ctx context.Context, userID, orderID string) error {
	businessID, err := s.businessIDOf(ctx, userID)
	if err != nil {
		return err
	}

	err = s.repo.UpdateOrderStatus(ctx, orderID, businessID, models.OrderStatusPaid)
	if err != nil {
		return apperror.Internal("Gagal mengupdate status pesanan")
	}
	return nil
}
