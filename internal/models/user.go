package models

import "time"

// User Model
type User struct {
	ID           string    `gorm:"type:varchar(36);primaryKey"`
	Email        string    `gorm:"unique;type:varchar(100);not null"`
	PasswordHash string    `gorm:"type:varchar(255);not null"`
	FirstName    string    `gorm:"type:varchar(50);not null"`
	LastName     string    `gorm:"type:varchar(50);not null"`
	Phone        string    `gorm:"type:varchar(20);not null"`
	CreatedAt    time.Time
	UpdatedAt    time.Time
}
