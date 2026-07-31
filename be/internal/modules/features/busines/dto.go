package busines



type RegisterBusinessRequest struct {
	Name          string `json:"name" binding:"required"`
	Description   string `json:"description"`
	Address       string `json:"address" binding:"required"`
	PhoneBusiness string `json:"phone_business" binding:"required"`
}



type RegisterBusinessResponse struct {
	ID string `json:"business_id"`
	Name string `json:"name"`
}


type CreateProductRequest struct {
	BusinessID string `json:"business_id"`
	Name string `json:"name"`
	Description string `json:"description"`
	Price string `json:"price"`
	Stock int `json:"stock"`
}

type CreateProductResponse struct {
	ID string `json:"product_id"`
	Name string `json:"name"`
}
