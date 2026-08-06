package busines

import (
	"baper/internal/common/apperror"
	"baper/internal/models"
	"context"
	"errors"
	"fmt"
)




type Service interface {
	RegisterBusiness(ctx context.Context,userID string, req RegisterBusinessRequest) (*RegisterBusinessResponse, error)
	GetMonthlyRecap(ctx context.Context, userID string, year, month int) (*MonthlyRecapResponse, error)
	ExportMonthlyRecap(ctx context.Context, userID string, year, month int) (string, error)
}

type service struct {
	repo Repository
}

func NewBusinessService(repo Repository) Service{
	return  &service{repo}
}

func (s *service) RegisterBusiness(ctx context.Context, userID string, req RegisterBusinessRequest) (*RegisterBusinessResponse, error) {
	// 1. Cek udah pernah daftar dengan nama sama belum
	existing, err := s.repo.ExistingBusiness(ctx, userID, req.Name)
	if err != nil {
		return nil, apperror.NotFound("Business belum pernah dibuat")
	}
	if existing != nil {
		return nil, apperror.Conflict("Kamu telah mendaftarkan bisnis dengan nama yang sama")
	}

	// 2. Bikin model buat di-insert ke DB (INI yang dikirim ke repository)
	business := &models.Business{
		Name:          req.Name,
		Description:   req.Description,
		Address:       req.Address,
		PhoneBusiness: req.PhoneBusiness,
	}

	if err := s.repo.CreateBusiness(ctx, userID, business); err != nil {
		return nil, err
	}

	return &RegisterBusinessResponse{
		ID:   business.ID,
		Name: business.Name,
	}, nil
}

func (s *service) GetMonthlyRecap(ctx context.Context, userID string, year, month int) (*MonthlyRecapResponse, error) {
	business, err := s.repo.FindBusinessByUserID(ctx, userID)
	if err != nil {
		return nil, apperror.NotFound("Bisnis tidak ditemukan untuk user ini")
	}

	startDate := fmt.Sprintf("%04d-%02d-01", year, month)
	endDate := fmt.Sprintf("%04d-%02d-01", year, month+1)
	if month == 12 {
		endDate = fmt.Sprintf("%04d-01-01", year+1)
	}

	recap, err := s.repo.GetMonthlyRecap(ctx, business.ID, startDate, endDate)
	if err != nil {
		return nil, errors.New("gagal mengambil rekap bulanan")
	}

	return recap, nil
}

func (s *service) ExportMonthlyRecap(ctx context.Context, userID string, year, month int) (string, error) {
	// 1. Get Recap Data (this can run concurrently if there were more heavy tasks, but here it's fast enough)
	recap, err := s.GetMonthlyRecap(ctx, userID, year, month)
	if err != nil {
		return "", err
	}

	// 2. Generate CSV
	csvData := "Bulan,Tahun,Total Pendapatan,Total Pesanan,Sudah Dibayar (Paid),Belum Dibayar (Unpaid)\n"
	csvData += fmt.Sprintf("%d,%d,%.2f,%d,%d,%d\n", month, year, recap.TotalRevenue, recap.TotalOrders, recap.TotalPaidOrders, recap.TotalUnpaidOrders)

	return csvData, nil
}
