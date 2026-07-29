package models

import "time"

// Product Model
type Product struct {
	ID          string    `gorm:"type:varchar(36);primaryKey"`
	BusinessID  string    `gorm:"type:varchar(36);not null"`
	Name        string    `gorm:"type:varchar(100);not null"`
	Description string    `gorm:"type:text"`
	Price       float64   `gorm:"type:decimal(15,2);not null"`
	Stock       int       `gorm:"type:int;not null;default:0"`
	CreatedAt   time.Time

	Business   Business    `gorm:"foreignKey:BusinessID"`
	OrderItems []OrderItem `gorm:"foreignKey:ProductID"`
}
