package models

import "time"

// Order Model
type Order struct {
	ID          string    `gorm:"type:varchar(36);primaryKey"`
	BusinessID  string    `gorm:"type:varchar(36);not null"`
	CustomerID  string    `gorm:"type:varchar(36);not null"`
	SessionID   string    `gorm:"type:varchar(36)"`
	TotalAmount float64   `gorm:"type:decimal(15,2);not null"`
	Status      string    `gorm:"type:varchar(50);not null"`
	CreatedAt   time.Time
	UpdatedAt   time.Time
}
