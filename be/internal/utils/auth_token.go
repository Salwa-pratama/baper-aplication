package utils

import (
	"errors"
	"fmt"
	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

var (
	ErrInvalidToken  = errors.New("invalid token")
	ErrClaimNotFound = errors.New("user_id claim not found")
	ErrSecretMissing = errors.New("JWT secret belum di-set di environment")
	ErrWrongTokenUse = errors.New("jenis token tidak sesuai")
)

const (
	accessTokenTTL  = time.Hour * 72
	refreshTokenTTL = time.Hour * 24 * 7
)

// accessSecret mengembalikan secret untuk access token.
// Kalau kosong kita HARUS gagal, bukan menandatangani dengan key kosong.
func accessSecret() ([]byte, error) {
	s := os.Getenv("JWT_SECRET")
	if s == "" {
		return nil, ErrSecretMissing
	}
	return []byte(s), nil
}

func refreshSecret() ([]byte, error) {
	s := os.Getenv("JWT_REFRESH_SECRET")
	if s == "" {
		return nil, ErrSecretMissing
	}
	return []byte(s), nil
}

// GenerateJWT membuat access token. Payload: user_id, email, type=access.
func GenerateJWT(userID string, email string) (string, error) {
	secret, err := accessSecret()
	if err != nil {
		return "", err
	}

	now := time.Now()
	claims := jwt.MapClaims{
		"user_id": userID,
		"email":   email,
		"type":    "access",
		"iat":     now.Unix(),
		"exp":     now.Add(accessTokenTTL).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(secret)
}

// GenerateRefreshToken membuat refresh token — umur panjang, payload minim, secret berbeda.
func GenerateRefreshToken(userID string) (string, error) {
	secret, err := refreshSecret()
	if err != nil {
		return "", err
	}

	now := time.Now()
	claims := jwt.MapClaims{
		"user_id": userID,
		"type":    "refresh",
		"iat":     now.Unix(),
		"exp":     now.Add(refreshTokenTTL).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(secret)
}

// parseVerified memverifikasi signature, algoritma, dan exp.
// jwt/v5 sudah otomatis menolak token expired lewat ParseWithClaims.
func parseVerified(tokenString string, secret []byte) (jwt.MapClaims, error) {
	claims := jwt.MapClaims{}

	_, err := jwt.ParseWithClaims(
		tokenString,
		claims,
		func(t *jwt.Token) (interface{}, error) {
			// Cegah algorithm-confusion: hanya terima HMAC.
			if _, ok := t.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, fmt.Errorf("algoritma tidak didukung: %v", t.Header["alg"])
			}
			return secret, nil
		},
		jwt.WithValidMethods([]string{jwt.SigningMethodHS256.Alg()}),
		jwt.WithExpirationRequired(),
	)
	if err != nil {
		return nil, ErrInvalidToken
	}

	return claims, nil
}

// GetUserIDFromToken memverifikasi access token lalu mengambil user_id.
// Token dengan signature salah, algoritma lain, atau sudah expired akan ditolak.
func GetUserIDFromToken(tokenString string) (string, error) {
	secret, err := accessSecret()
	if err != nil {
		return "", err
	}

	claims, err := parseVerified(tokenString, secret)
	if err != nil {
		return "", err
	}

	// Refresh token tidak boleh dipakai sebagai access token.
	if t, ok := claims["type"].(string); ok && t == "refresh" {
		return "", ErrWrongTokenUse
	}

	userID, ok := claims["user_id"].(string)
	if !ok || userID == "" {
		// fallback ke "sub" kalau pakai standard claim
		userID, ok = claims["sub"].(string)
		if !ok || userID == "" {
			return "", ErrClaimNotFound
		}
	}

	return userID, nil
}

// ValidateRefreshToken memverifikasi refresh token dan memastikan type-nya benar.
func ValidateRefreshToken(tokenString string) (string, error) {
	secret, err := refreshSecret()
	if err != nil {
		return "", err
	}

	claims, err := parseVerified(tokenString, secret)
	if err != nil {
		return "", err
	}

	if t, ok := claims["type"].(string); !ok || t != "refresh" {
		return "", ErrWrongTokenUse
	}

	userID, ok := claims["user_id"].(string)
	if !ok || userID == "" {
		return "", ErrClaimNotFound
	}

	return userID, nil
}
