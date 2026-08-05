package models

import (
	"github.com/google/uuid"
	"gorm.io/gorm"
)

// OrderItem Model
type OrderItem struct {
	ID        string  `gorm:"type:varchar(36);primaryKey" json:"id"`
	OrderID   string  `gorm:"type:varchar(36);not null" json:"order_id"`
	ProductID string  `gorm:"type:varchar(36);not null" json:"product_id"`
	Quantity  int     `gorm:"type:int;not null;default:1" json:"quantity"`
	Price     float64 `gorm:"type:decimal(15,2);not null" json:"price"`

	Order   Order   `gorm:"foreignKey:OrderID" json:"order"`
	Product Product `gorm:"foreignKey:ProductID" json:"product"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (oi *OrderItem) BeforeCreate(tx *gorm.DB) error {
	if oi.ID == "" {
		oi.ID = uuid.New().String()
	}
	return nil
}
