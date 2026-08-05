package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Product Model
type Product struct {
	ID          string    `gorm:"type:varchar(36);primaryKey" json:"id"`
	BusinessID  string    `gorm:"type:varchar(36);not null" json:"business_id"`
	Name        string    `gorm:"type:varchar(100);not null" json:"name"`
	Description string    `gorm:"type:text" json:"description"`
	Price       float64   `gorm:"type:decimal(15,2);not null" json:"price"`
	Stock       int       `gorm:"type:int;not null;default:0" json:"stock"`
	CreatedAt   time.Time `json:"created_at"`

	Business   Business    `gorm:"foreignKey:BusinessID" json:"business"`
	OrderItems []OrderItem `gorm:"foreignKey:ProductID" json:"order_items"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (p *Product) BeforeCreate(tx *gorm.DB) error {
	if p.ID == "" {
		p.ID = uuid.New().String()
	}
	return nil
}
