package login

import (
	"gorm.io/gorm"
)

// Interface
type Repository interface {
	SayHay(value string) (string, error)
}


// Struct
type repository struct {
	db *gorm.DB
}


// Main Function
func NewLoginRepository(db *gorm.DB) Repository {
	return  &repository{db}
}


// Implement Method

func (r repository) SayHay(value string)(string, error) {
	return "Hallo ini adalah api login " + value, nil
}
