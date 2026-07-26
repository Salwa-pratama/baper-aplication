package utils

import (
	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

// helper function untuk membuat JWT
func GenerateJWT(userID string, email string) (string, error) {

	var jwtSecret = os.Getenv("JWT_SECRET")

	claims := jwt.MapClaims{
		"user_id": userID,
		"email":   email,
		"exp":     time.Now().Add(time.Hour * 72).Unix(), // expired dalam 72 jam
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(jwtSecret))
}
