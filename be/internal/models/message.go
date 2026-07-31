package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// Message Model
type Message struct {
	ID         string    `gorm:"type:varchar(36);primaryKey"`
	SessionID  string    `gorm:"type:varchar(36);not null"`
	SenderType string    `gorm:"type:varchar(50);not null"`
	Content    string    `gorm:"type:text;not null"`
	Metadata   string    `gorm:"type:json"`
	CreatedAt  time.Time

	ChatSession ChatSession `gorm:"foreignKey:SessionID"`
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

