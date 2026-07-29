package models

import "time"

// Business Model
type Business struct {
	ID            string    `gorm:"type:varchar(36);primaryKey"`
	UserID        string    `gorm:"type:varchar(36);not null"`
	Name          string    `gorm:"type:varchar(100);not null"`
	Description   string    `gorm:"type:text"`
	Address       string    `gorm:"type:text"`
	PhoneBusiness string    `gorm:"type:varchar(20)"`
	CreatedAt     time.Time

	User         User          `gorm:"foreignKey:UserID"`
	Bots         []Bot         `gorm:"foreignKey:BusinessID"`
	BotDocuments []BotDocument `gorm:"foreignKey:BusinessID"`
	Customers    []Customer    `gorm:"foreignKey:BusinessID"`
	Products     []Product     `gorm:"foreignKey:BusinessID"`
	Orders       []Order       `gorm:"foreignKey:BusinessID"`
}
