package utils

import (
	"errors"
	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

var (
	ErrInvalidToken = errors.New("invalid token")
	ErrClaimNotFound = errors.New("user_id claim not found")
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


// Refresh token - umur panjang, payload minim
func GenerateRefreshToken(userID string) (string, error) {
	var refreshSecret = os.Getenv("JWT_REFRESH_SECRET") // beda secret!

	claims := jwt.MapClaims{
		"user_id": userID,
		"type":    "refresh",
		"exp":     time.Now().Add(time.Hour * 24 * 7).Unix(), // 7 hari
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(refreshSecret))
}


// (misal karena verifikasi udah dilakuin di middleware sebelumnya)
func GetUserIDFromToken(tokenString string) (string, error) {
	token, _, err := jwt.NewParser().ParseUnverified(tokenString, jwt.MapClaims{})
	if err != nil {
		return "", ErrInvalidToken
	}

	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return "", ErrInvalidToken
	}

	userID, ok := claims["user_id"].(string)
	if !ok {
		// coba fallback ke "sub" kalau pakai standard claim
		userID, ok = claims["sub"].(string)
		if !ok {
			return "", ErrClaimNotFound
		}
	}

	return userID, nil
}
