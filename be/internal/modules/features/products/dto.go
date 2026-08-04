package products



type CreateProductRequest struct {
	BusinessID string `json:"business_id"`
	Name string `json:"name"`
	Description string `json:"description"`
	Price string `json:"price"`
	Stock int `json:"stock"`
}

type ProductData struct {
	ID string `json:"product_id"`
	Name string `json:"name"`
}
