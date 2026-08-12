package conversation

import (
	"baper/internal/models"
	"context"
	"time"

	"gorm.io/gorm"
)

// sessionRow = hasil join sesi + customer + ringkasan pesan terakhir.
// Sengaja dipakai struct pipih supaya cukup satu query, bukan N+1
// (satu query per sesi untuk mengambil pesan terakhirnya).
type sessionRow struct {
	SessionID  string
	BotID      string
	CustomerID string
	Status     string
	StartedAt  time.Time
	EndedAt    *time.Time

	CustomerName  string
	CustomerPhone string

	LastMessage       string
	LastMessageSender string
	LastMessageAt     *time.Time
	MessageCount      int64
}

type Repository interface {
	// FindBusinessByUserID menentukan business_id dari JWT, bukan dari klien.
	FindBusinessByUserID(ctx context.Context, userID string) (*models.Business, error)

	// ListSessionsByBusiness mengembalikan daftar sesi milik satu bisnis,
	// diurutkan dari aktivitas terbaru.
	ListSessionsByBusiness(ctx context.Context, businessID string) ([]sessionRow, error)

	// FindSessionOwned mengambil sesi HANYA jika bot pemiliknya milik bisnis
	// tersebut. Mengembalikan gorm.ErrRecordNotFound kalau bukan miliknya.
	FindSessionOwned(ctx context.Context, businessID, sessionID string) (*sessionRow, error)

	// ListMessagesBySession mengembalikan pesan dalam satu sesi, urut naik.
	ListMessagesBySession(ctx context.Context, sessionID string, limit, offset int) ([]models.Message, error)
}

type repository struct {
	db *gorm.DB
}

func NewConversationRepository(db *gorm.DB) Repository {
	return &repository{db}
}

func (r *repository) FindBusinessByUserID(ctx context.Context, userID string) (*models.Business, error) {
	var business models.Business
	if err := r.db.WithContext(ctx).Where("user_id = ?", userID).First(&business).Error; err != nil {
		return nil, err
	}
	return &business, nil
}

// lastMsg membangun subquery berkorelasi yang mengambil satu kolom dari
// pesan TERBARU sebuah sesi. Dipakai sebagai satu-satunya definisi "pesan
// terakhir" — kalau ditulis manual berulang, cukup satu typo ORDER BY untuk
// membuat preview menampilkan pesan pertama.
//
// Kenapa subquery dan bukan loop per sesi: loop = N+1 query.
func lastMsg(col string) string {
	return "(SELECT m." + col + " FROM messages m WHERE m.session_id = cs.id ORDER BY m.created_at DESC LIMIT 1)"
}

// selectSessionSummary: SELECT yang dipakai bersama oleh list dan detail.
var selectSessionSummary = `
	cs.id          AS session_id,
	cs.bot_id      AS bot_id,
	cs.customer_id AS customer_id,
	cs.status      AS status,
	cs.started_at  AS started_at,
	cs.ended_at    AS ended_at,
	c.name            AS customer_name,
	c.wa_phone_number AS customer_phone,
	COALESCE(` + lastMsg("content") + `, '') AS last_message,
	COALESCE(` + lastMsg("sender_type") + `, '') AS last_message_sender,
	` + lastMsg("created_at") + ` AS last_message_at,
	(SELECT COUNT(*) FROM messages m WHERE m.session_id = cs.id) AS message_count
`

// baseSessionQuery: sesi -> bot -> customer, discope ke satu bisnis.
// Scope lewat bots.business_id inilah yang mencegah user membaca percakapan
// bisnis orang lain.
func (r *repository) baseSessionQuery(ctx context.Context, businessID string) *gorm.DB {
	return r.db.WithContext(ctx).
		Table("chat_sessions AS cs").
		Joins("JOIN bots b ON b.id = cs.bot_id").
		Joins("JOIN customers c ON c.id = cs.customer_id").
		Where("b.business_id = ?", businessID).
		Select(selectSessionSummary)
}

func (r *repository) ListSessionsByBusiness(ctx context.Context, businessID string) ([]sessionRow, error) {
	var rows []sessionRow

	err := r.baseSessionQuery(ctx, businessID).
		// Urut dari aktivitas terbaru; sesi tanpa pesan memakai started_at.
		Order("COALESCE(" + lastMsg("created_at") + ", cs.started_at) DESC").
		Scan(&rows).Error

	return rows, err
}

func (r *repository) FindSessionOwned(ctx context.Context, businessID, sessionID string) (*sessionRow, error) {
	var rows []sessionRow

	err := r.baseSessionQuery(ctx, businessID).
		Where("cs.id = ?", sessionID).
		Limit(1).
		Scan(&rows).Error
	if err != nil {
		return nil, err
	}

	// Scan tidak mengembalikan ErrRecordNotFound, jadi cek manual.
	// Sesi milik bisnis lain jatuh ke sini juga → diperlakukan tidak ada.
	if len(rows) == 0 {
		return nil, gorm.ErrRecordNotFound
	}

	return &rows[0], nil
}

func (r *repository) ListMessagesBySession(ctx context.Context, sessionID string, limit, offset int) ([]models.Message, error) {
	var msgs []models.Message
	err := r.db.WithContext(ctx).
		Where("session_id = ?", sessionID).
		Order("created_at ASC").
		Limit(limit).
		Offset(offset).
		Find(&msgs).Error
	return msgs, err
}
