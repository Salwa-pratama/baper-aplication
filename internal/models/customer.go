package models

import "time"

// Customer Model
type Customer struct {
	ID            string    `gorm:"type:varchar(36);primaryKey"`
	BusinessID    string    `gorm:"type:varchar(36);not null"`
	WaPhoneNumber string    `gorm:"type:varchar(20);not null"`
	Name          string    `gorm:"type:varchar(100)"`
	Address       string    `gorm:"type:text"`
	CreatedAt     time.Time
	UpdatedAt     time.Time
}
