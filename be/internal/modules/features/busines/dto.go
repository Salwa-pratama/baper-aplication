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


