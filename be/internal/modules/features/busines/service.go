package busines

import (
	"baper/internal/common/apperror"
	"baper/internal/models"
	"context"
	"fmt"
	"time"
)

type Service interface {
	RegisterBusiness(ctx context.Context, userID string, req RegisterBusinessRequest) (*RegisterBusinessResponse, error)
	GetMonthlyRecap(ctx context.Context, userID string, year, month int) (*MonthlyRecapResponse, error)
	ExportMonthlyRecap(ctx context.Context, userID string, year, month int) (string, error)
}

type service struct {
	repo Repository
}

func NewBusinessService(repo Repository) Service {
	return &service{repo}
}

func (s *service) RegisterBusiness(ctx context.Context, userID string, req RegisterBusinessRequest) (*RegisterBusinessResponse, error) {
	if userID == "" {
		return nil, apperror.Unauthorized("user tidak dikenali")
	}
	if req.Name == "" {
		return nil, apperror.BadRequest("Nama bisnis wajib diisi")
	}

	// 1. Cek udah pernah daftar dengan nama sama belum.
	// ExistingBusiness mengembalikan (nil, nil) kalau tidak ketemu, jadi
	// err != nil berarti error DB sungguhan — bukan "belum pernah dibuat".
	existing, err := s.repo.ExistingBusiness(ctx, userID, req.Name)
	if err != nil {
		return nil, apperror.Internal("Gagal memeriksa data bisnis")
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
	if userID == "" {
		return nil, apperror.Unauthorized("user tidak dikenali")
	}
	if month < 1 || month > 12 || year < 1970 || year > 9999 {
		return nil, apperror.BadRequest("Parameter year dan month tidak valid")
	}

	business, err := s.repo.FindBusinessByUserID(ctx, userID)
	if err != nil {
		return nil, apperror.NotFound("Bisnis tidak ditemukan untuk user ini")
	}

	// Rentang tanggal: [awal bulan ini, awal bulan depan).
	// time.AddDate menangani pergantian tahun tanpa perlu kasus khusus.
	start := time.Date(year, time.Month(month), 1, 0, 0, 0, 0, time.UTC)
	end := start.AddDate(0, 1, 0)

	startDate := start.Format("2006-01-02")
	endDate := end.Format("2006-01-02")

	recap, err := s.repo.GetMonthlyRecap(ctx, business.ID, startDate, endDate)
	if err != nil {
		return nil, apperror.Internal("gagal mengambil rekap bulanan")
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
