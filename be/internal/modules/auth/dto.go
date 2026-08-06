package auth

type RegisterRequest struct {
	Name            string `json:"name" validate:"required"`
	Email           string `json:"email" validate:"required,email"`
	Password        string `json:"password" validate:"required,min=6"`
	BusinessName    string `json:"business_name" validate:"required"`
	BusinessDesc    string `json:"business_description"`
	BusinessAddress string `json:"business_address"`
	BusinessPhone   string `json:"business_phone"`
}

type RegisterResponse struct {
	Status bool `json:"status"`
	Message string `json:"message"`
}

type LoginRequest struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required"`
}


type UserResponse struct {
	ID string `json:"id"`
	Name string `json:"name"`
}
type AuthData struct {
	AccessToken string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	User UserResponse `json:"user"`
}


type AuthResponse struct {
	Status bool `json:"status"`
	Message string `json:"message"`
	Data AuthData `json:"data"`
}
