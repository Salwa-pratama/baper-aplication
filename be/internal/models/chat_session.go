package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// ChatSession Model
type ChatSession struct {
	ID         string     `gorm:"type:varchar(36);primaryKey" json:"id"`
	BotID      string     `gorm:"type:varchar(36);not null" json:"bot_id"`
	CustomerID string     `gorm:"type:varchar(36);not null" json:"customer_id"`
	StartedAt  time.Time  `json:"started_at"`
	EndedAt    *time.Time `json:"ended_at"`
	Status     string     `gorm:"type:varchar(50)" json:"status"`

	Bot              Bot               `gorm:"foreignKey:BotID" json:"bot"`
	Customer         Customer          `gorm:"foreignKey:CustomerID" json:"customer"`
	Messages         []Message         `gorm:"foreignKey:SessionID" json:"messages"`
	OrderRecapDrafts []OrderRecapDraft `gorm:"foreignKey:SessionID" json:"order_recap_drafts"`
	Orders           []Order           `gorm:"foreignKey:SessionID" json:"orders"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (cs *ChatSession) BeforeCreate(tx *gorm.DB) error {
	if cs.ID == "" {
		cs.ID = uuid.New().String()
	}
	return nil
}
