package conversation

import (
	"context"
	"errors"

	"baper/internal/common/apperror"
	"baper/internal/models"

	"gorm.io/gorm"
)

// Batas paging. Klien boleh minta lebih kecil, tapi tidak lebih besar —
// tanpa batas, satu request bisa menarik seluruh riwayat chat.
const (
	defaultSessionLimit = 50
	maxSessionLimit     = 100
	defaultMessageLimit = 100
	maxMessageLimit     = 200
)

// Semua method menerima userID dari JWT dan memakainya untuk menentukan
// bisnis mana yang boleh dibaca. Tidak ada business_id dari klien.
type Service interface {
	ListConversations(ctx context.Context, userID string) ([]ConversationResponse, error)
	GetConversationMessages(ctx context.Context, userID, sessionID string, limit, offset int) (*ConversationDetailResponse, error)
}

type service struct {
	repo Repository
}

func NewConversationService(repo Repository) Service {
	return &service{repo}
}

// paging menormalkan limit/offset dari klien: limit dijaga dalam rentang
// wajar, offset negatif dianggap 0.
func paging(limit, offset, def, max int) (int, int) {
	switch {
	case limit <= 0:
		limit = def
	case limit > max:
		limit = max
	}
	if offset < 0 {
		offset = 0
	}
	return limit, offset
}

// businessIDOf mengambil bisnis milik user yang sedang login.
func (s *service) businessIDOf(ctx context.Context, userID string) (string, error) {
	if userID == "" {
		return "", apperror.Unauthorized("user tidak dikenali")
	}

	business, err := s.repo.FindBusinessByUserID(ctx, userID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return "", apperror.NotFound("Bisnis tidak ditemukan untuk user ini")
		}
		return "", apperror.Internal("Gagal mengambil data bisnis")
	}
	return business.ID, nil
}

func (s *service) ListConversations(ctx context.Context, userID string) ([]ConversationResponse, error) {
	businessID, err := s.businessIDOf(ctx, userID)
	if err != nil {
		return nil, err
	}

	rows, err := s.repo.ListSessionsByBusiness(ctx, businessID)
	if err != nil {
		return nil, apperror.Internal("Gagal mengambil daftar percakapan")
	}

	out := make([]ConversationResponse, 0, len(rows))
	for i := range rows {
		out = append(out, toConversationResponse(&rows[i]))
	}
	return out, nil
}

func (s *service) GetConversationMessages(ctx context.Context, userID, sessionID string, limit, offset int) (*ConversationDetailResponse, error) {
	if sessionID == "" {
		return nil, apperror.BadRequest("ID sesi wajib diisi")
	}

	businessID, err := s.businessIDOf(ctx, userID)
	if err != nil {
		return nil, err
	}

	limit, offset = paging(limit, offset, defaultMessageLimit, maxMessageLimit)

	// Kepemilikan diperiksa LEBIH DULU. Sesi milik bisnis lain dijawab 404
	// (bukan 403) supaya tidak membocorkan keberadaan percakapan orang lain.
	row, err := s.repo.FindSessionOwned(ctx, businessID, sessionID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apperror.NotFound("Percakapan tidak ditemukan")
		}
		return nil, apperror.Internal("Gagal mengambil data percakapan")
	}

	msgs, err := s.repo.ListMessagesBySession(ctx, sessionID, limit, offset)
	if err != nil {
		return nil, apperror.Internal("Gagal mengambil pesan")
	}

	items := make([]MessageResponse, 0, len(msgs))
	for i := range msgs {
		items = append(items, toMessageResponse(&msgs[i]))
	}

	return &ConversationDetailResponse{
		SessionID:     row.SessionID,
		CustomerID:    row.CustomerID,
		CustomerName:  row.CustomerName,
		CustomerPhone: row.CustomerPhone,
		Status:        row.Status,
		Messages:      items,
	}, nil
}

func toConversationResponse(r *sessionRow) ConversationResponse {
	return ConversationResponse{
		SessionID:         r.SessionID,
		BotID:             r.BotID,
		CustomerID:        r.CustomerID,
		Status:            r.Status,
		StartedAt:         r.StartedAt,
		EndedAt:           r.EndedAt,
		CustomerName:      r.CustomerName,
		CustomerPhone:     r.CustomerPhone,
		LastMessage:       r.LastMessage,
		LastMessageSender: r.LastMessageSender,
		LastMessageAt:     r.LastMessageAt,
		MessageCount:      r.MessageCount,
	}
}

func toMessageResponse(m *models.Message) MessageResponse {
	return MessageResponse{
		ID:         m.ID,
		SenderType: m.SenderType,
		Content:    m.Content,
		Metadata:   m.Metadata,
		CreatedAt:  m.CreatedAt,
		SessionID:  m.SessionID,
	}
}
