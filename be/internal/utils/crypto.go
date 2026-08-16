package utils

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/base64"
	"io"
	"os"
)

// getEncryptionKey mengembalikan 32-byte key.
// Jika ENCRYPTION_KEY kosong, mengembalikan fallback key (HANYA UNTUK DEV, tidak aman untuk prod).
func getEncryptionKey() []byte {
	key := os.Getenv("ENCRYPTION_KEY")
	if key == "" {
		// Fallback 32-byte key untuk development
		key = "BaperAppSuperSecretKeyForAes256!"
	}
	// Pastikan key berukuran persis 32 byte untuk AES-256
	keyBytes := []byte(key)
	if len(keyBytes) > 32 {
		keyBytes = keyBytes[:32]
	} else if len(keyBytes) < 32 {
		padded := make([]byte, 32)
		copy(padded, keyBytes)
		keyBytes = padded
	}
	return keyBytes
}

// Encrypt mengenkripsi teks menggunakan AES-256-GCM.
// Output berupa Base64 (nonce + ciphertext).
func Encrypt(plaintext string) string {
	if plaintext == "" {
		return ""
	}
	
	// Untuk media caption atau teks tertentu, jika sudah berupa metadata (misal: [IMAGE]), kita tetap enkripsi
	// asalkan konsisten.
	
	key := getEncryptionKey()
	block, err := aes.NewCipher(key)
	if err != nil {
		// Jika gagal membuat cipher, kembalikan plaintext agar aplikasi tidak crash total
		return plaintext
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return plaintext
	}

	nonce := make([]byte, aesGCM.NonceSize())
	if _, err = io.ReadFull(rand.Reader, nonce); err != nil {
		return plaintext
	}

	ciphertext := aesGCM.Seal(nonce, nonce, []byte(plaintext), nil)
	return base64.StdEncoding.EncodeToString(ciphertext)
}

// Decrypt mendekripsi ciphertext Base64.
// Jika gagal dekripsi (karena format salah, teks lama yang belum dienkripsi, dll),
// akan mengembalikan ciphertext aslinya (backward compatibility).
func Decrypt(ciphertext string) string {
	if ciphertext == "" {
		return ""
	}

	data, err := base64.StdEncoding.DecodeString(ciphertext)
	if err != nil {
		// Bukan base64 valid, kemungkinan pesan lama (plaintext)
		return ciphertext
	}

	key := getEncryptionKey()
	block, err := aes.NewCipher(key)
	if err != nil {
		return ciphertext
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return ciphertext
	}

	nonceSize := aesGCM.NonceSize()
	if len(data) < nonceSize {
		// Terlalu pendek untuk mengandung nonce, kemungkinan plaintext kebetulan valid base64
		return ciphertext
	}

	nonce, ciphertextBytes := data[:nonceSize], data[nonceSize:]
	plaintext, err := aesGCM.Open(nil, nonce, ciphertextBytes, nil)
	if err != nil {
		// Dekripsi gagal (kunci salah atau data rusak/lama), kembalikan asli
		return ciphertext
	}

	return string(plaintext)
}
