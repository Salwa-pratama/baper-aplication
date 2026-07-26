package auth

type RegisterRequest struct {
	FirstName string `json:"first_name" validate:"required"`
	LastName  string `json:"last_name" validate:"required"`
	Phone     string `json:"phone" validate:"required"`
	Email     string `json:"email" validate:"required,email"`
	Password  string `json:"password" validate:"required,min=6"`
}

type RegisterResponse struct {
	Status string `json:"status"`
	Message string `json:"message"`
}

type LoginRequest struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required"`
}


type UserResponse struct {
	ID string `json:"id"`
	Name string `json:name`
}
type AuthData struct {
	AccessToken string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	User UserResponse `json:"user"`
}


type AuthResponse struct {
	Status string `json:"status"`
	Message string `json:"message"`
	Data AuthData `json:"data"`
}
