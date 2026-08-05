package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

// OrderRecapDraft Model
type OrderRecapDraft struct {
	ID              string    `gorm:"type:varchar(36);primaryKey" json:"id"`
	SessionID       string    `gorm:"type:varchar(36);not null" json:"session_id"`
	RawExtraction   string    `gorm:"type:text" json:"raw_extraction"`
	ConfidenceScore float64   `gorm:"type:decimal(5,2)" json:"confidence_score"`
	Status          string    `gorm:"type:varchar(50)" json:"status"`
	CreatedAt       time.Time `json:"created_at"`

	ChatSession ChatSession `gorm:"foreignKey:SessionID" json:"chat_session"`
}

// Hook ini otomatis kepanggil GORM sebelum proses INSERT
func (ord *OrderRecapDraft) BeforeCreate(tx *gorm.DB) error {
	if ord.ID == "" {
		ord.ID = uuid.New().String()
	}
	return nil
}
