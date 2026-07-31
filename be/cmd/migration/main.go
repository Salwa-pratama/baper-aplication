package main

import (
	"baper/config"
	"baper/internal/database/migrations"
	"log"
)


func main() {
	log.Printf("Running AutoMigrate....")
	db := config.DbSupabase()

	migration_id, err := migrations.Migration001(db)

	if err != nil {
		log.Fatal("Migration failed : ", err)
	}


	log.Printf("Migrasi berhasil dengan migration id : " + migration_id)
}
