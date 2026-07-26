package auth

import (
	"baper/internal/models"

	"gorm.io/gorm"
)

// Interface
type Repository interface {
	FindByEmail(email string) (*models.User, error)
	CreateUser(user *models.User) error
}


// Struct
type repository struct {
	db *gorm.DB
}


// Main Function
func NewLoginRepository(db *gorm.DB) Repository {
	return  &repository{db}
}




// Untuk mencari user berdasarkan email
func (r *repository) FindByEmail(email string) (*models.User, error ) {
	var user models.User
	err := r.db.Where("email = ?", email).First(&user).Error
	return &user, err
}


// Untuk register akun baru
func (r *repository) CreateUser(user *models.User) error {
	return r.db.Create(user).Error
}
