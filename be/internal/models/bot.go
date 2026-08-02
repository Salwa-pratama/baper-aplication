package models

import "time"

// Bot Model
type Bot struct {
	ID                  string    `gorm:"type:varchar(36);primaryKey"`
	BusinessID          string    `gorm:"type:varchar(36);not null"`
	Name                string    `gorm:"type:varchar(100);not null"`
	WaStatus            string    `gorm:"type:varchar(50)"`
	AgentPrompt         string    `gorm:"type:text"`
	CreatedAt           time.Time
	UpdatedAt           time.Time

	Business     Business      `gorm:"foreignKey:BusinessID"`
	ChatSessions []ChatSession `gorm:"foreignKey:BotID"`
}
