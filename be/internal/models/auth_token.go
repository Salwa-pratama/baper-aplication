package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Token AuthToken juga tidak boleh bocor ke response JSON.
type AuthToken struct {
	ID        string    `gorm:"type:varchar(36);primaryKey" json:"id"`
	UserID    string    `gorm:"type:varchar(36);not null" json:"user_id"`
	Token     string    `gorm:"type:text;not null" json:"-"`
	ExpiresAt time.Time `json:"expires_at"`
	CreatedAt time.Time `json:"created_at"`

	User User `gorm:"foreignKey:UserID" json:"user,omitempty"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (at *AuthToken) BeforeCreate(tx *gorm.DB) error {
	if at.ID == "" {
		at.ID = uuid.New().String()
	}
	return nil
}
