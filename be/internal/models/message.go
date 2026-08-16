package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Message Model
type Message struct {
	ID         string    `gorm:"type:varchar(36);primaryKey" json:"id"`
	SessionID  string    `gorm:"type:varchar(36);not null" json:"session_id"`
	SenderType string    `gorm:"type:varchar(50);not null" json:"sender_type"`
	Content    string    `gorm:"type:text;not null" json:"content"`
	Metadata   string    `gorm:"type:json" json:"metadata"`
	IsRead     bool      `gorm:"default:false" json:"is_read"`
	CreatedAt  time.Time `json:"created_at"`

	ChatSession ChatSession `gorm:"foreignKey:SessionID" json:"chat_session"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (m *Message) BeforeCreate(tx *gorm.DB) error {
	if m.ID == "" {
		m.ID = uuid.New().String()
	}
	// Postgres will reject empty string "" for JSON type, so we use valid JSON "{}"
	if m.Metadata == "" {
		m.Metadata = "{}"
	}
	return nil
}
