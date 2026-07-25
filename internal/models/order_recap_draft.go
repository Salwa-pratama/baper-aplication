package models

import "time"

// OrderRecapDraft Model
type OrderRecapDraft struct {
	ID              string    `gorm:"type:varchar(36);primaryKey"`
	SessionID       string    `gorm:"type:varchar(36);not null"`
	RawExtraction   string    `gorm:"type:text"`
	ConfidenceScore float64   `gorm:"type:decimal(5,2)"`
	Status          string    `gorm:"type:varchar(50)"`
	CreatedAt       time.Time
}
