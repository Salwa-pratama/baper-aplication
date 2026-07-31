package models

import "time"

// BotDocument Model
type BotDocument struct {
	ID            string    `gorm:"type:varchar(36);primaryKey"`
	BusinessID    string    `gorm:"type:varchar(36);not null"`
	FileName      string    `gorm:"type:varchar(255);not null"`
	FileType      string    `gorm:"type:varchar(50)"`
	VectorStoreID string    `gorm:"type:varchar(100)"`
	Status        string    `gorm:"type:varchar(50)"`
	UploadedAt    time.Time

	Business Business `gorm:"foreignKey:BusinessID"`
}
