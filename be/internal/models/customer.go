package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Customer Model
type Customer struct {
	ID            string    `gorm:"type:varchar(36);primaryKey"`
	BusinessID    string    `gorm:"type:varchar(36);not null"`
	WaPhoneNumber string    `gorm:"type:varchar(20);not null"`
	Name          string    `gorm:"type:varchar(100)"`
	Address       string    `gorm:"type:text"`
	CreatedAt     time.Time
	UpdatedAt     time.Time

	Business     Business      `gorm:"foreignKey:BusinessID"`
	ChatSessions []ChatSession `gorm:"foreignKey:CustomerID"`
	Orders       []Order       `gorm:"foreignKey:CustomerID"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (c *Customer) BeforeCreate(tx *gorm.DB) error {
	if c.ID == "" {
		c.ID = uuid.New().String()
	}
	return nil
}
