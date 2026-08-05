package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Bot Model
type Bot struct {
	ID                  string         `gorm:"type:varchar(36);primaryKey" json:"id"`
	BusinessID          string         `gorm:"type:varchar(36);not null" json:"business_id"`
	Name                string         `gorm:"type:varchar(100);not null" json:"name"`
	WaStatus            string         `gorm:"type:varchar(50)" json:"wa_status"`
	AgentPrompt         string         `gorm:"type:text" json:"agent_prompt"`
	CreatedAt           time.Time      `json:"created_at"`

	UpdatedAt           time.Time      `json:"updated_at"`

	Business            Business       `gorm:"foreignKey:BusinessID" json:"business"`
	ChatSessions        []ChatSession  `gorm:"foreignKey:BotID" json:"chat_sessions"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (b *Bot) BeforeCreate(tx *gorm.DB) error {
	if b.ID == "" {
		b.ID = uuid.New().String()
	}
	return nil
}
