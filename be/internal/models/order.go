package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)
type OrderStatus string

const (
	OrderStatusPaid    OrderStatus = "paid"
	OrderStatusUnpaid  OrderStatus = "unpaid"
)

// Optional: validasi manual
func (s OrderStatus) IsValid() bool {
	switch s {
	case OrderStatusPaid, OrderStatusUnpaid:
		return true
	}
	return false
}

type Order struct {
	ID          string      `gorm:"type:varchar(36);primaryKey" json:"id"`
	BusinessID  string      `gorm:"type:varchar(36);not null" json:"business_id"`
	CustomerID  string      `gorm:"type:varchar(36);not null" json:"customer_id"`
	SessionID   *string     `gorm:"type:varchar(36)" json:"session_id"`
	TotalAmount float64     `gorm:"type:decimal(15,2);not null" json:"total_amount"`
	Status      OrderStatus `gorm:"type:varchar(50);not null" json:"status"`
	CreatedAt   time.Time   `json:"created_at"`
	UpdatedAt   time.Time   `json:"updated_at"`

	Business    Business    `gorm:"foreignKey:BusinessID" json:"business"`
	Customer    Customer    `gorm:"foreignKey:CustomerID" json:"customer"`
	ChatSession ChatSession `gorm:"foreignKey:SessionID" json:"chat_session"`
	OrderItems  []OrderItem `gorm:"foreignKey:OrderID" json:"order_items"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (o *Order) BeforeCreate(tx *gorm.DB) error {
	if o.ID == "" {
		o.ID = uuid.New().String()
	}
	return nil
}
