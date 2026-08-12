package utils

import (
	"os"
	"testing"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

const (
	testSecret        = "test-access-secret"
	testRefreshSecret = "test-refresh-secret"
)

func setSecrets(t *testing.T) {
	t.Setenv("JWT_SECRET", testSecret)
	t.Setenv("JWT_REFRESH_SECRET", testRefreshSecret)
}

func signWith(t *testing.T, secret string, claims jwt.MapClaims) string {
	t.Helper()
	tok := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	s, err := tok.SignedString([]byte(secret))
	if err != nil {
		t.Fatalf("gagal sign token: %v", err)
	}
	return s
}

// Token yang sah harus diterima dan user_id-nya benar.
func TestGetUserIDFromToken_ValidToken(t *testing.T) {
	setSecrets(t)

	tok, err := GenerateJWT("user-123", "a@b.com")
	if err != nil {
		t.Fatalf("GenerateJWT error: %v", err)
	}

	uid, err := GetUserIDFromToken(tok)
	if err != nil {
		t.Fatalf("token sah kok ditolak: %v", err)
	}
	if uid != "user-123" {
		t.Errorf("user_id = %q, mau %q", uid, "user-123")
	}
}

// Inti temuan #1: signature salah harus DITOLAK.
func TestGetUserIDFromToken_RejectsWrongSignature(t *testing.T) {
	setSecrets(t)

	forged := signWith(t, "secret-ngawur-punya-penyerang", jwt.MapClaims{
		"user_id": "ATTACKER",
		"type":    "access",
		"exp":     time.Now().Add(time.Hour).Unix(),
	})

	if uid, err := GetUserIDFromToken(forged); err == nil {
		t.Fatalf("token dengan signature salah DITERIMA sebagai %q", uid)
	}
}

// Token expired harus ditolak walau signature-nya benar.
func TestGetUserIDFromToken_RejectsExpired(t *testing.T) {
	setSecrets(t)

	expired := signWith(t, testSecret, jwt.MapClaims{
		"user_id": "user-123",
		"type":    "access",
		"exp":     time.Now().Add(-time.Hour).Unix(),
	})

	if uid, err := GetUserIDFromToken(expired); err == nil {
		t.Fatalf("token expired DITERIMA sebagai %q", uid)
	}
}

// Token tanpa klaim exp harus ditolak (WithExpirationRequired).
func TestGetUserIDFromToken_RejectsMissingExp(t *testing.T) {
	setSecrets(t)

	noExp := signWith(t, testSecret, jwt.MapClaims{
		"user_id": "user-123",
		"type":    "access",
	})

	if uid, err := GetUserIDFromToken(noExp); err == nil {
		t.Fatalf("token tanpa exp DITERIMA sebagai %q", uid)
	}
}

// Serangan alg=none harus ditolak.
func TestGetUserIDFromToken_RejectsAlgNone(t *testing.T) {
	setSecrets(t)

	tok := jwt.NewWithClaims(jwt.SigningMethodNone, jwt.MapClaims{
		"user_id": "ATTACKER",
		"exp":     time.Now().Add(time.Hour).Unix(),
	})
	unsigned, err := tok.SignedString(jwt.UnsafeAllowNoneSignatureType)
	if err != nil {
		t.Fatalf("gagal bikin token alg=none: %v", err)
	}

	if uid, err := GetUserIDFromToken(unsigned); err == nil {
		t.Fatalf("token alg=none DITERIMA sebagai %q", uid)
	}
}

// Refresh token tidak boleh dipakai sebagai access token.
func TestGetUserIDFromToken_RejectsRefreshToken(t *testing.T) {
	setSecrets(t)

	// Refresh token ditandatangani dengan refresh secret, jadi pakai access
	// secret pun signature-nya sudah salah. Uji juga kasus type=refresh yang
	// ditandatangani dengan access secret.
	refreshLike := signWith(t, testSecret, jwt.MapClaims{
		"user_id": "user-123",
		"type":    "refresh",
		"exp":     time.Now().Add(time.Hour).Unix(),
	})

	if uid, err := GetUserIDFromToken(refreshLike); err == nil {
		t.Fatalf("token type=refresh DITERIMA sebagai access token (%q)", uid)
	}
}

// Kalau JWT_SECRET kosong, jangan pernah menandatangani/menerima apa pun.
func TestSecretMissing(t *testing.T) {
	os.Unsetenv("JWT_SECRET")
	t.Setenv("JWT_SECRET", "")

	if _, err := GenerateJWT("u", "e"); err == nil {
		t.Error("GenerateJWT sukses padahal JWT_SECRET kosong")
	}
	if _, err := GetUserIDFromToken("apa.saja.token"); err == nil {
		t.Error("GetUserIDFromToken sukses padahal JWT_SECRET kosong")
	}
}

// Refresh token yang sah harus lolos ValidateRefreshToken.
func TestValidateRefreshToken(t *testing.T) {
	setSecrets(t)

	tok, err := GenerateRefreshToken("user-777")
	if err != nil {
		t.Fatalf("GenerateRefreshToken error: %v", err)
	}

	uid, err := ValidateRefreshToken(tok)
	if err != nil {
		t.Fatalf("refresh token sah ditolak: %v", err)
	}
	if uid != "user-777" {
		t.Errorf("user_id = %q, mau %q", uid, "user-777")
	}

	// Access token tidak boleh lolos sebagai refresh token.
	access, _ := GenerateJWT("user-777", "a@b.com")
	if _, err := ValidateRefreshToken(access); err == nil {
		t.Error("access token DITERIMA oleh ValidateRefreshToken")
	}
}
