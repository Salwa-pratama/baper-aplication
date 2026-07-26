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
	Register(req RegisterRequest) (AuthResponse, error)
}

type service struct {
	repo Repository
}

func NewLoginService(repo Repository) Service {
	return &service{repo}
}

func (s *service) Register(req RegisterRequest) (AuthResponse, error) {
	// 1. Cek apakah email sudah terdaftar
	existingUser, _ := s.repo.FindByEmail(req.Email)
	if existingUser != nil && existingUser.ID != "" {
		return AuthResponse{}, errors.New("email sudah terdaftar")
	}

	// 2. Hash Password
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return AuthResponse{}, errors.New("gagal mengenkripsi password")
	}

	// 3. Buat User baru
	newUser := &models.User{
		ID:           uuid.New().String(),
		Email:        req.Email,
		PasswordHash: string(hashedPassword),
		FirstName:    req.FirstName,
		LastName:     req.LastName,
		Phone:        req.Phone,
	}

	// 4. Simpan ke database
	if err := s.repo.CreateUser(newUser); err != nil {
		return AuthResponse{}, errors.New("gagal menyimpan data user")
	}

	// 5. Generate Token
	token, err := utils.GenerateJWT(newUser.ID, newUser.Email)
	if err != nil {
		return AuthResponse{}, errors.New("gagal membuat token")
	}

	return AuthResponse{
		Token: token,
		User:  newUser,
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

	return AuthResponse{
		Token: token,
		User:  user,
	}, nil
}




