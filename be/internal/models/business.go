package models

import (
	"time"

	"github.com/google/uuid"

	"gorm.io/gorm"
)

// Business Model
type Business struct {
	ID            string    `gorm:"type:varchar(36);primaryKey" json:"id"`
	UserID        string    `gorm:"type:varchar(36);uniqueIndex;not null" json:"user_id"`
	Name          string    `gorm:"type:varchar(100);not null" json:"name"`
	Description   string    `gorm:"type:text" json:"description"`
	Address       string    `gorm:"type:text" json:"address"`
	PhoneBusiness string    `gorm:"type:varchar(20)" json:"phone_business"`
	CreatedAt     time.Time `json:"created_at"`

	User         User          `gorm:"foreignKey:UserID" json:"user"`
	Bots         []Bot         `gorm:"foreignKey:BusinessID" json:"bots"`
	Customers    []Customer    `gorm:"foreignKey:BusinessID" json:"customers"`
	Products     []Product     `gorm:"foreignKey:BusinessID" json:"products"`
	Orders       []Order       `gorm:"foreignKey:BusinessID" json:"orders"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (b *Business) BeforeCreate(tx *gorm.DB) error {
	if b.ID == "" {
		b.ID = uuid.New().String()
	}
	return nil
}
