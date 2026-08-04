package products

import (
	"baper/internal/common/apperror"
	"baper/internal/models"
	"context"
	"errors"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type Service interface {
	CreateProduct(ctx context.Context, req CreateProductRequest) (*ProductResponse, error)
	GetAllProducts(ctx context.Context, businessID string) ([]ProductResponse, error)
	GetProductByID(ctx context.Context, id string) (*ProductResponse, error)
	UpdateProduct(ctx context.Context, id string, req UpdateProductRequest) (*ProductResponse, error)
	DeleteProduct(ctx context.Context, id string) error
}

type service struct {
	repo Repository
}

func NewProductService(repo Repository) Service {
	return &service{repo}
}

func (s *service) CreateProduct(ctx context.Context, req CreateProductRequest) (*ProductResponse, error) {
	if req.BusinessID == "" || req.Name == "" || req.Price <= 0 {
		return nil, apperror.BadRequest("BusinessID, Name, dan Price (harus > 0) wajib diisi")
	}

	product := &models.Product{
		ID:          uuid.New().String(),
		BusinessID:  req.BusinessID,
		Name:        req.Name,
		Description: req.Description,
		Price:       req.Price,
		Stock:       req.Stock,
	}

	if err := s.repo.CreateProduct(ctx, product); err != nil {
		return nil, apperror.Internal("Gagal membuat produk")
	}

	return s.mapToResponse(product), nil
}

func (s *service) GetAllProducts(ctx context.Context, businessID string) ([]ProductResponse, error) {
	products, err := s.repo.FindAllProducts(ctx, businessID)
	if err != nil {
		return nil, apperror.Internal("Gagal mengambil data produk")
	}

	responses := make([]ProductResponse, 0, len(products))
	for _, p := range products {
		responses = append(responses, *s.mapToResponse(&p))
	}

	return responses, nil
}

func (s *service) GetProductByID(ctx context.Context, id string) (*ProductResponse, error) {
	product, err := s.repo.FindProductByID(ctx, id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apperror.NotFound("Produk tidak ditemukan")
		}
		return nil, apperror.Internal("Gagal mengambil data produk")
	}

	return s.mapToResponse(product), nil
}

func (s *service) UpdateProduct(ctx context.Context, id string, req UpdateProductRequest) (*ProductResponse, error) {
	if req.Name == "" || req.Price <= 0 {
		return nil, apperror.BadRequest("Name dan Price (harus > 0) wajib diisi")
	}

	product, err := s.repo.FindProductByID(ctx, id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apperror.NotFound("Produk tidak ditemukan")
		}
		return nil, apperror.Internal("Gagal mengambil data produk")
	}

	product.Name = req.Name
	product.Description = req.Description
	product.Price = req.Price
	product.Stock = req.Stock

	if err := s.repo.UpdateProduct(ctx, product); err != nil {
		return nil, apperror.Internal("Gagal update produk")
	}

	return s.mapToResponse(product), nil
}

func (s *service) DeleteProduct(ctx context.Context, id string) error {
	product, err := s.repo.FindProductByID(ctx, id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return apperror.NotFound("Produk tidak ditemukan")
		}
		return apperror.Internal("Gagal mengambil data produk")
	}

	if err := s.repo.DeleteProduct(ctx, product.ID); err != nil {
		return apperror.Internal("Gagal menghapus produk")
	}

	return nil
}

func (s *service) mapToResponse(p *models.Product) *ProductResponse {
	return &ProductResponse{
		ID:          p.ID,
		BusinessID:  p.BusinessID,
		Name:        p.Name,
		Description: p.Description,
		Price:       p.Price,
		Stock:       p.Stock,
		CreatedAt:   p.CreatedAt,
	}
}
