package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// User Model
type User struct {
	ID           string    `gorm:"type:varchar(36);primaryKey" json:"id"`
	Email        string    `gorm:"unique;type:varchar(100);not null" json:"email"`
	PasswordHash string    `gorm:"type:varchar(255);not null" json:"password_hash"`
	Name         string    `gorm:"type:varchar(100);not null" json:"name"`
	CreatedAt    time.Time `json:"created_at"`
	UpdatedAt    time.Time `json:"updated_at"`

	AuthTokens []AuthToken `gorm:"foreignKey:UserID" json:"auth_tokens"`
	Business *Business  `gorm:"foreignKey:UserID" json:"business"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (u *User) BeforeCreate(tx *gorm.DB) error {
	if u.ID == "" {
		u.ID = uuid.New().String()
	}
	return nil
}
