package auth

import (
	"baper/internal/models"
	"baper/internal/utils"
	"errors"

	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"
)


type Service interface {
	SignIn(req LoginRequest) (AuthResponse, error)
	Register(req RegisterRequest) (RegisterResponse, error)
}

type service struct {
	repo Repository
}

func NewLoginService(repo Repository) Service {
	return &service{repo}
}

func (s *service) Register(req RegisterRequest) (RegisterResponse, error) {
	// 1. Cek apakah email sudah terdaftar
	existingUser, _ := s.repo.FindByEmail(req.Email)
	if existingUser != nil && existingUser.ID != "" {
		return RegisterResponse{}, errors.New("email sudah terdaftar")
	}

	// 2. Hash Password
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return RegisterResponse{}, errors.New("gagal mengenkripsi password")
	}

	// 3. Buat User baru
	newUser := &models.User{
		ID:           uuid.New().String(),
		Email:        req.Email,
		PasswordHash: string(hashedPassword),
		FirstName:    req.FirstName,
		LastName:     req.LastName,
	}

	// 4. Buat Bisnis baru
	newBusiness := &models.Business{
		ID:            uuid.New().String(),
		Name:          req.BusinessName,
		Description:   req.BusinessDesc,
		Address:       req.BusinessAddress,
		PhoneBusiness: req.BusinessPhone,
	}

	// 5. Simpan ke database (User dan Bisnis) menggunakan transaksi
	if err := s.repo.CreateUserAndBusiness(newUser, newBusiness); err != nil {
		return RegisterResponse{}, errors.New("gagal menyimpan data user dan bisnis")
	}

	return  RegisterResponse{
		Status: true,
		Message: "Register successfully",
	}, nil
}

func (s *service) SignIn(req LoginRequest) (AuthResponse, error) {
	// 1. Cari user berdasarkan email
	user, err := s.repo.FindByEmail(req.Email)
	if err != nil {
		return AuthResponse{}, errors.New("email atau password salah")
	}

	// 2. Bandingkan password
	err = bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password))
	if err != nil {
		return AuthResponse{}, errors.New("email atau password salah")
	}

	// 3. Generate Token
	token, err := utils.GenerateJWT(user.ID, user.Email)
	if err != nil {
		return AuthResponse{}, errors.New("gagal membuat token")
	}


	refresh_token, err := utils.GenerateRefreshToken(user.ID)
	if err != nil {
		return AuthResponse{}, errors.New("Gagal membuat refresh token")
	}


	return AuthResponse{
		Status : true,
		Message: "Login Successfully",
		Data : AuthData{
			AccessToken: token,
			RefreshToken: refresh_token,
			User: UserResponse{
				ID : user.ID,
				Name: user.FirstName + user.LastName,
			},
		},
	}, nil
}




