package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Customer Model
type Customer struct {
	ID            string    `gorm:"type:varchar(36);primaryKey" json:"id"`
	BusinessID    string    `gorm:"type:varchar(36);not null" json:"business_id"`
	WaPhoneNumber string    `gorm:"type:varchar(20);not null" json:"wa_phone_number"`
	Name          string    `gorm:"type:varchar(100)" json:"name"`
	Address       string    `gorm:"type:text" json:"address"`
	CreatedAt     time.Time `json:"created_at"`
	UpdatedAt     time.Time `json:"updated_at"`

	Business     Business      `gorm:"foreignKey:BusinessID" json:"business"`
	ChatSessions []ChatSession `gorm:"foreignKey:CustomerID" json:"chat_sessions"`
	Orders       []Order       `gorm:"foreignKey:CustomerID" json:"orders"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (c *Customer) BeforeCreate(tx *gorm.DB) error {
	if c.ID == "" {
		c.ID = uuid.New().String()
	}
	return nil
}
