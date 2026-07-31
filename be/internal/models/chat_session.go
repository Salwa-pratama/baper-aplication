package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

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

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (cs *ChatSession) BeforeCreate(tx *gorm.DB) error {
	if cs.ID == "" {
		cs.ID = uuid.New().String()
	}
	return nil
}

