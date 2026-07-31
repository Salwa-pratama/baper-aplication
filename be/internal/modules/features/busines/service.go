package busines

import (
	"baper/internal/common/apperror"
	"baper/internal/models"
	"context"
)




type Service interface {
	RegisterBusiness(ctx context.Context,userID string, req RegisterBusinessRequest) (*RegisterBusinessResponse, error)
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


