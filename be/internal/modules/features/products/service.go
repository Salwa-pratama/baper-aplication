package products

import (
	"baper/internal/common/apperror"
	"baper/internal/models"
	"context"
	"errors"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Semua method menerima userID (dari JWT) dan memakainya untuk
// menentukan/memvalidasi kepemilikan. Tidak ada lagi business_id dari client.
type Service interface {
	CreateProduct(ctx context.Context, userID string, req CreateProductRequest) (*ProductResponse, error)
	GetAllProducts(ctx context.Context, userID string) ([]ProductResponse, error)
	GetProductByID(ctx context.Context, userID string, id string) (*ProductResponse, error)
	UpdateProduct(ctx context.Context, userID string, id string, req UpdateProductRequest) (*ProductResponse, error)
	DeleteProduct(ctx context.Context, userID string, id string) error
}

type service struct {
	repo Repository
}

func NewProductService(repo Repository) Service {
	return &service{repo}
}

// businessIDOf mengambil business milik user yang sedang login.
func (s *service) businessIDOf(ctx context.Context, userID string) (string, error) {
	if userID == "" {
		return "", apperror.Unauthorized("user tidak dikenali")
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

// findOwnedProduct mengambil produk HANYA jika produk itu milik bisnis user.
// Produk milik orang lain dijawab 404 (bukan 403) supaya tidak membocorkan
// keberadaan data orang lain.
func (s *service) findOwnedProduct(ctx context.Context, userID, id string) (*models.Product, error) {
	businessID, err := s.businessIDOf(ctx, userID)
	if err != nil {
		return nil, err
	}

	product, err := s.repo.FindProductByID(ctx, id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apperror.NotFound("Produk tidak ditemukan")
		}
		return nil, apperror.Internal("Gagal mengambil data produk")
	}

	if product.BusinessID != businessID {
		return nil, apperror.NotFound("Produk tidak ditemukan")
	}

	return product, nil
}

func (s *service) CreateProduct(ctx context.Context, userID string, req CreateProductRequest) (*ProductResponse, error) {
	if req.Name == "" || req.Price <= 0 {
		return nil, apperror.BadRequest("Name dan Price (harus > 0) wajib diisi")
	}
	if req.Stock < 0 {
		return nil, apperror.BadRequest("Stock tidak boleh negatif")
	}

	businessID, err := s.businessIDOf(ctx, userID)
	if err != nil {
		return nil, err
	}

	product := &models.Product{
		ID:          uuid.New().String(),
		BusinessID:  businessID,
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

func (s *service) GetAllProducts(ctx context.Context, userID string) ([]ProductResponse, error) {
	businessID, err := s.businessIDOf(ctx, userID)
	if err != nil {
		return nil, err
	}

	products, err := s.repo.FindAllProducts(ctx, businessID)
	if err != nil {
		return nil, apperror.Internal("Gagal mengambil data produk")
	}

	responses := make([]ProductResponse, 0, len(products))
	for i := range products {
		responses = append(responses, *s.mapToResponse(&products[i]))
	}

	return responses, nil
}

func (s *service) GetProductByID(ctx context.Context, userID string, id string) (*ProductResponse, error) {
	product, err := s.findOwnedProduct(ctx, userID, id)
	if err != nil {
		return nil, err
	}
	return s.mapToResponse(product), nil
}

func (s *service) UpdateProduct(ctx context.Context, userID string, id string, req UpdateProductRequest) (*ProductResponse, error) {
	if req.Name == "" || req.Price <= 0 {
		return nil, apperror.BadRequest("Name dan Price (harus > 0) wajib diisi")
	}
	if req.Stock < 0 {
		return nil, apperror.BadRequest("Stock tidak boleh negatif")
	}

	product, err := s.findOwnedProduct(ctx, userID, id)
	if err != nil {
		return nil, err
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

func (s *service) DeleteProduct(ctx context.Context, userID string, id string) error {
	product, err := s.findOwnedProduct(ctx, userID, id)
	if err != nil {
		return err
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
