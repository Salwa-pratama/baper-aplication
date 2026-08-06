package main

import (
	"baper/internal/config"
	"baper/internal/models"
	"fmt"
	"log"
	"os"

	"github.com/google/uuid"
	"github.com/joho/godotenv"
	"golang.org/x/crypto/bcrypt"
)

func main() {
	// Load environment variables
	err := godotenv.Load("../../.env")
	if err != nil {
		// Coba path yang lain jika dieksekusi dari root
		err = godotenv.Load(".env")
		if err != nil {
			log.Println("Peringatan: Gagal memuat file .env, menggunakan variabel environment yang ada")
		}
	}

	db := config.DbSupabase()

	fmt.Println("🚀 Memulai proses seeding database...")

	// 1. Setup User Data
	password := "password123"
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		log.Fatalf("Gagal nge-hash password: %v", err)
	}

	user := models.User{
		ID:           uuid.New().String(),
		Email:        "admin@baper.com",
		PasswordHash: string(hashedPassword),
		FirstName:    "Admin",
		LastName:     "Baper",
	}

	// Cek apakah user sudah ada
	var existingUser models.User
	if err := db.Where("email = ?", user.Email).First(&existingUser).Error; err == nil {
		fmt.Println("⚠️ User admin@baper.com sudah ada, menggunakan user yang ada.")
		user = existingUser
	} else {
		if err := db.Create(&user).Error; err != nil {
			log.Fatalf("Gagal membuat user: %v", err)
		}
		fmt.Println("✅ User berhasil dibuat!")
	}

	// 2. Setup Business Data
	business := models.Business{
		ID:            uuid.New().String(),
		UserID:        user.ID,
		Name:          "Bisnis Baper Official",
		Description:   "Toko resmi untuk pengujian Baper bot",
		Address:       "Jl. Teknologi No.1",
		PhoneBusiness: "081234567890",
	}

	var existingBusiness models.Business
	if err := db.Where("user_id = ?", user.ID).First(&existingBusiness).Error; err == nil {
		fmt.Println("⚠️ Bisnis untuk user ini sudah ada, menggunakan bisnis yang ada.")
		business = existingBusiness
	} else {
		if err := db.Create(&business).Error; err != nil {
			log.Fatalf("Gagal membuat bisnis: %v", err)
		}
		fmt.Println("✅ Business berhasil dibuat!")
	}

	// 3. Setup Bot Data
	waNumber := os.Getenv("PHONE_NUMBER_ID")
	if waNumber == "" {
		waNumber = "1290607887458203" // fallback jika env kosong
	}

	bot := models.Bot{
		ID:          uuid.New().String(),
		BusinessID:  business.ID,
		Name:        "Baper AI Assistant",
		WaNumber:    waNumber,
		WaStatus:    "connected",
		AgentPrompt: "Kamu adalah asisten Baper Official. Jawab dengan ramah dan informatif.",
		IsActive:    true,
	}

	var existingBot models.Bot
	if err := db.Where("business_id = ?", business.ID).First(&existingBot).Error; err == nil {
		fmt.Println("⚠️ Bot untuk bisnis ini sudah ada, memperbarui data Bot...")
		existingBot.WaNumber = waNumber // update waNumber dari .env
		db.Save(&existingBot)
	} else {
		if err := db.Create(&bot).Error; err != nil {
			log.Fatalf("Gagal membuat bot: %v", err)
		}
		fmt.Println("✅ Bot berhasil dibuat dengan nomor:", waNumber)
	}

	fmt.Println("🎉 Seeding selesai dengan sukses!")
	fmt.Println("Detail Akun Uji Coba:")
	fmt.Println("Email    : admin@baper.com")
	fmt.Println("Password : password123")
	fmt.Println("WaNumber :", waNumber)
}
