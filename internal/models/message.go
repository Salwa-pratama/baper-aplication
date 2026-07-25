package models

import "time"

// Message Model
type Message struct {
	ID         string    `gorm:"type:varchar(36);primaryKey"`
	SessionID  string    `gorm:"type:varchar(36);not null"`
	SenderType string    `gorm:"type:varchar(50);not null"`
	Content    string    `gorm:"type:text;not null"`
	Metadata   string    `gorm:"type:json"`
	CreatedAt  time.Time
}
