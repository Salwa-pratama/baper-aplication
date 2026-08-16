package conversation

import "time"

// ConversationResponse = satu "card" di daftar chat.
// Dipakai untuk layar Home: nama customer + pesan terakhir + waktu.
type ConversationResponse struct {
	SessionID  string     `json:"session_id"`
	BotID      string     `json:"bot_id"`
	CustomerID string     `json:"customer_id"`
	Status     string     `json:"status"`
	StartedAt  time.Time  `json:"started_at"`
	EndedAt    *time.Time `json:"ended_at"`

	// Data customer, di-flatten supaya klien tidak perlu nested lookup.
	CustomerName  string `json:"customer_name"`
	CustomerPhone string `json:"customer_phone"`

	// Ringkasan pesan terakhir untuk preview di card.
	LastMessage       string     `json:"last_message"`
	LastMessageSender string     `json:"last_message_sender"`
	LastMessageAt     *time.Time `json:"last_message_at"`

	// MessageCount = total pesan dalam sesi ini.
	MessageCount int64 `json:"message_count"`

	// Jumlah pesan yang dikirim customer dan belum dibaca
	UnreadCount int64 `json:"unread_count"`
}

// MessageResponse = satu bubble chat.
type MessageResponse struct {
	ID string `json:"id"`
	// SenderType bernilai "customer" atau "bot" (lihat chat/service.go).
	SenderType string    `json:"sender_type"`
	Content    string    `json:"content"`
	Metadata   string    `json:"metadata"`
	CreatedAt  time.Time `json:"created_at"`
	SessionID  string    `json:"session_id"`
}

// ConversationDetailResponse = isi percakapan lengkap beserta identitas customer,
// supaya layar detail chat cukup satu kali hit API.
type ConversationDetailResponse struct {
	SessionID     string            `json:"session_id"`
	CustomerID    string            `json:"customer_id"`
	CustomerName  string            `json:"customer_name"`
	CustomerPhone string            `json:"customer_phone"`
	Status        string            `json:"status"`
	Messages      []MessageResponse `json:"messages"`
}
