package models

import "time"

// AuthToken Model
type AuthToken struct {
	ID        string    `gorm:"type:varchar(36);primaryKey"`
	UserID    string    `gorm:"type:varchar(36);not null"`
	Token     string    `gorm:"type:text;not null"`
	ExpiresAt time.Time
	CreatedAt time.Time

	User User `gorm:"foreignKey:UserID"`
}


