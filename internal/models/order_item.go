package models

// OrderItem Model
type OrderItem struct {
	ID        string  `gorm:"type:varchar(36);primaryKey"`
	OrderID   string  `gorm:"type:varchar(36);not null"`
	ProductID string  `gorm:"type:varchar(36);not null"`
	Quantity  int     `gorm:"type:int;not null;default:1"`
	Price     float64 `gorm:"type:decimal(15,2);not null"`
}
