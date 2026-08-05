package migrations

import (
	"baper/internal/models"

	"gorm.io/gorm"
)




func Migration001(db *gorm.DB) (string, error) {
	migration_id := "001"
	err := db.AutoMigrate(
		&models.User{},
		&models.AuthToken{},
		&models.Business{},
		&models.Bot{},
		&models.Customer{},
		&models.Product{},
		&models.ChatSession{},
		&models.Order{},
		&models.Message{},
		&models.OrderRecapDraft{},
		&models.OrderItem{},
	)
	return migration_id, err
}
