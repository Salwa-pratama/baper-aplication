package models

import "time"

// ChatSession Model
type ChatSession struct {
	ID         string    `gorm:"type:varchar(36);primaryKey"`
	BotID      string    `gorm:"type:varchar(36);not null"`
	CustomerID string    `gorm:"type:varchar(36);not null"`
	StartedAt  time.Time
	EndedAt    *time.Time
	Status     string    `gorm:"type:varchar(50)"`

	Bot              Bot               `gorm:"foreignKey:BotID"`
	Customer         Customer          `gorm:"foreignKey:CustomerID"`
	Messages         []Message         `gorm:"foreignKey:SessionID"`
	OrderRecapDrafts []OrderRecapDraft `gorm:"foreignKey:SessionID"`
	Orders           []Order           `gorm:"foreignKey:SessionID"`
}
