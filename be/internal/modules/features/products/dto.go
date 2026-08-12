package products

import "time"

// CreateProductRequest — business_id sengaja DIHAPUS dari body.
// Backend menentukannya dari JWT, supaya user tidak bisa menitipkan
// produk ke bisnis milik orang lain.
type CreateProductRequest struct {
	Name        string  `json:"name" validate:"required"`
	Description string  `json:"description"`
	Price       float64 `json:"price" validate:"required"`
	Stock       int     `json:"stock"`
}

type UpdateProductRequest struct {
	Name        string  `json:"name" validate:"required"`
	Description string  `json:"description"`
	Price       float64 `json:"price" validate:"required"`
	Stock       int     `json:"stock"`
}

type ProductResponse struct {
	ID          string    `json:"id"`
	BusinessID  string    `json:"business_id"`
	Name        string    `json:"name"`
	Description string    `json:"description"`
	Price       float64   `json:"price"`
	Stock       int       `json:"stock"`
	CreatedAt   time.Time `json:"created_at"`
}
