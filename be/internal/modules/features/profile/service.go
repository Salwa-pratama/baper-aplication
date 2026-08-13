package profile

import (
	"baper/internal/common/apperror"
	"context"
	"strconv"
	"time"
)

type Service interface {
	GetProfile(ctx context.Context, userID string) (*ProfileResponse, error)
}

type service struct {
	repo Repository
}

func NewProfileService(repo Repository) Service {
	return &service{repo}
}

// Indonesian month names for formatting
var indonesianMonths = []string{
	"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
	"Jul", "Agu", "Sep", "Okt", "Nov", "Des",
}

func formatMonthYear(t time.Time) string {
	month := indonesianMonths[t.Month()]
	year := t.Year()
	// Output: "Jan 2024"
	return month + " " + strconv.Itoa(year)
}

func (s *service) GetProfile(ctx context.Context, userID string) (*ProfileResponse, error) {
	if userID == "" {
		return nil, apperror.Unauthorized("User tidak dikenali")
	}

	user, business, err := s.repo.GetUserProfile(ctx, userID)
	if err != nil {
		return nil, apperror.Internal("Gagal mengambil data profil")
	}

	resp := &ProfileResponse{
		Name:       user.Name,
		Email:      user.Email,
		JoinedDate: formatMonthYear(user.CreatedAt),
	}

	if business != nil {
		resp.BusinessName = business.Name
		resp.Address = business.Address
		resp.Phone = business.PhoneBusiness

		totalOrders, _ := s.repo.GetTotalOrders(ctx, business.ID)
		totalCustomers, _ := s.repo.GetTotalCustomers(ctx, business.ID)

		resp.TotalOrders = totalOrders
		resp.TotalCustomers = totalCustomers
	}

	return resp, nil
}
