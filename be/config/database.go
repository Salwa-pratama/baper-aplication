package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func DbSupabase() *gorm.DB {

	godotenv.Overload()

	dsn := os.Getenv("SUPABASE_URL")

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatal("Gagale terkoneksi dengan supabase server !!")
	}

	log.Printf("Telah terkoneksi dengan baik ke supabase!")

	return db
}
