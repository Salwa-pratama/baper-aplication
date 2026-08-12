package conversation

import (
	"context"
	"errors"
	"testing"
	"time"

	"baper/internal/common/apperror"
	"baper/internal/models"

	"gorm.io/gorm"
)

// fakeRepo: dua bisnis. userA -> bizA (punya sesiA), userB -> bizB (punya sesiB).
// Inti pengujian: userB tidak boleh bisa membaca percakapan milik bizA.
type fakeRepo struct {
	businessByUser map[string]*models.Business
	// sessionsByBusiness meniru scope JOIN bots.business_id di repository asli.
	sessionsByBusiness map[string][]sessionRow
	messagesBySession  map[string][]models.Message

	// dicatat untuk membuktikan query pesan tidak pernah dijalankan
	// ketika kepemilikan gagal.
	messageQueries []string
	lastLimit      int
	lastOffset     int
}

func newFakeRepo() *fakeRepo {
	t0 := time.Date(2026, 8, 11, 9, 0, 0, 0, time.UTC)
	t1 := t0.Add(time.Minute)

	return &fakeRepo{
		businessByUser: map[string]*models.Business{
			"userA": {ID: "bizA", UserID: "userA", Name: "Toko A"},
			"userB": {ID: "bizB", UserID: "userB", Name: "Toko B"},
		},
		sessionsByBusiness: map[string][]sessionRow{
			"bizA": {{
				SessionID:         "sesiA",
				BotID:             "botA",
				CustomerID:        "custA",
				Status:            "active",
				StartedAt:         t0,
				CustomerName:      "Siti Reseller",
				CustomerPhone:     "628111",
				LastMessage:       "Paket A ready?",
				LastMessageSender: "customer",
				LastMessageAt:     &t1,
				MessageCount:      2,
			}},
			"bizB": {{
				SessionID:     "sesiB",
				BotID:         "botB",
				CustomerID:    "custB",
				Status:        "active",
				StartedAt:     t0,
				CustomerName:  "Agus Sembako",
				CustomerPhone: "628222",
				MessageCount:  0,
			}},
		},
		messagesBySession: map[string][]models.Message{
			"sesiA": {
				{ID: "m1", SessionID: "sesiA", SenderType: "customer", Content: "Paket A ready?", Metadata: "{}", CreatedAt: t0},
				{ID: "m2", SessionID: "sesiA", SenderType: "bot", Content: "Ready kak", Metadata: "{}", CreatedAt: t1},
			},
			"sesiB": {
				{ID: "m3", SessionID: "sesiB", SenderType: "customer", Content: "rahasia bizB", Metadata: "{}", CreatedAt: t0},
			},
		},
	}
}

func (f *fakeRepo) FindBusinessByUserID(_ context.Context, userID string) (*models.Business, error) {
	b, ok := f.businessByUser[userID]
	if !ok {
		return nil, gorm.ErrRecordNotFound
	}
	return b, nil
}

func (f *fakeRepo) ListSessionsByBusiness(_ context.Context, businessID string) ([]sessionRow, error) {
	return f.sessionsByBusiness[businessID], nil
}

func (f *fakeRepo) FindSessionOwned(_ context.Context, businessID, sessionID string) (*sessionRow, error) {
	for _, r := range f.sessionsByBusiness[businessID] {
		if r.SessionID == sessionID {
			row := r
			return &row, nil
		}
	}
	// Sesi milik bisnis lain jatuh ke sini juga — tidak dibedakan.
	return nil, gorm.ErrRecordNotFound
}

func (f *fakeRepo) ListMessagesBySession(_ context.Context, sessionID string, limit, offset int) ([]models.Message, error) {
	f.messageQueries = append(f.messageQueries, sessionID)
	f.lastLimit, f.lastOffset = limit, offset
	return f.messagesBySession[sessionID], nil
}

func appErrCode(t *testing.T, err error) int {
	t.Helper()
	var ae *apperror.AppError
	if !errors.As(err, &ae) {
		t.Fatalf("error bukan *apperror.AppError: %#v", err)
	}
	return ae.Code
}

// Daftar chat harus membawa nama customer + preview pesan terakhir.
func TestListConversations_BawaNamaCustomerDanPesanTerakhir(t *testing.T) {
	svc := NewConversationService(newFakeRepo())

	list, err := svc.ListConversations(context.Background(), "userA")
	if err != nil {
		t.Fatalf("list gagal: %v", err)
	}
	if len(list) != 1 {
		t.Fatalf("jumlah = %d, mau 1", len(list))
	}

	got := list[0]
	if got.CustomerName != "Siti Reseller" {
		t.Errorf("customer_name = %q, mau %q", got.CustomerName, "Siti Reseller")
	}
	if got.CustomerPhone != "628111" {
		t.Errorf("customer_phone = %q, mau %q", got.CustomerPhone, "628111")
	}
	if got.LastMessage != "Paket A ready?" {
		t.Errorf("last_message = %q, mau %q", got.LastMessage, "Paket A ready?")
	}
	if got.LastMessageAt == nil {
		t.Error("last_message_at nil, mau ada waktunya")
	}
	if got.SessionID != "sesiA" {
		t.Errorf("session_id = %q, mau sesiA (dipakai klien untuk hit detail)", got.SessionID)
	}
}

// Daftar chat hanya boleh berisi percakapan bisnis sendiri.
func TestListConversations_ScopedKeBisnisSendiri(t *testing.T) {
	svc := NewConversationService(newFakeRepo())

	list, err := svc.ListConversations(context.Background(), "userB")
	if err != nil {
		t.Fatalf("list gagal: %v", err)
	}
	for _, c := range list {
		if c.SessionID == "sesiA" {
			t.Errorf("percakapan bisnis lain ikut terbawa: %+v", c)
		}
	}
	if len(list) != 1 || list[0].SessionID != "sesiB" {
		t.Errorf("hasil = %+v, mau hanya sesiB", list)
	}
}

// Alur utama: card ditekan -> ambil pesan dengan customer yang diklik.
func TestGetConversationMessages_PemilikDapatIsiChat(t *testing.T) {
	repo := newFakeRepo()
	svc := NewConversationService(repo)

	detail, err := svc.GetConversationMessages(context.Background(), "userA", "sesiA", 0, 0)
	if err != nil {
		t.Fatalf("get gagal: %v", err)
	}
	if detail.CustomerName != "Siti Reseller" {
		t.Errorf("customer_name = %q, mau %q", detail.CustomerName, "Siti Reseller")
	}
	if len(detail.Messages) != 2 {
		t.Fatalf("jumlah pesan = %d, mau 2", len(detail.Messages))
	}
	// Urutan naik: pesan pertama harus yang paling lama.
	if detail.Messages[0].ID != "m1" || detail.Messages[1].ID != "m2" {
		t.Errorf("urutan pesan salah: %+v", detail.Messages)
	}
	if detail.Messages[0].SenderType != "customer" || detail.Messages[1].SenderType != "bot" {
		t.Errorf("sender_type tidak terbawa benar: %+v", detail.Messages)
	}
}

// Temuan Lapis 2 diterapkan di modul baru: userB tidak boleh baca chat bizA.
func TestGetConversationMessages_BisnisLainDijawab404(t *testing.T) {
	repo := newFakeRepo()
	svc := NewConversationService(repo)

	_, err := svc.GetConversationMessages(context.Background(), "userB", "sesiA", 0, 0)
	if err == nil {
		t.Fatal("userB BISA membaca percakapan bizA")
	}
	if code := appErrCode(t, err); code != 404 {
		t.Errorf("code = %d, mau 404 (jangan bocorkan keberadaan percakapan)", code)
	}
	// Yang paling penting: query pesan tidak pernah dijalankan sama sekali,
	// jadi isi chat bizA tidak pernah tersentuh.
	if len(repo.messageQueries) != 0 {
		t.Errorf("query pesan tetap jalan padahal bukan pemilik: %v", repo.messageQueries)
	}
}

func TestGetConversationMessages_SesiTidakAda(t *testing.T) {
	svc := NewConversationService(newFakeRepo())

	_, err := svc.GetConversationMessages(context.Background(), "userA", "sesiNgawur", 0, 0)
	if err == nil {
		t.Fatal("sesi tidak ada LOLOS")
	}
	if code := appErrCode(t, err); code != 404 {
		t.Errorf("code = %d, mau 404", code)
	}
}

func TestUserIDKosongDitolak(t *testing.T) {
	svc := NewConversationService(newFakeRepo())
	ctx := context.Background()

	if _, err := svc.ListConversations(ctx, ""); err == nil {
		t.Error("list dengan userID kosong LOLOS")
	} else if code := appErrCode(t, err); code != 401 {
		t.Errorf("list: code = %d, mau 401", code)
	}

	if _, err := svc.GetConversationMessages(ctx, "", "sesiA", 0, 0); err == nil {
		t.Error("get dengan userID kosong LOLOS")
	} else if code := appErrCode(t, err); code != 401 {
		t.Errorf("get: code = %d, mau 401", code)
	}
}

func TestSessionIDKosongDitolak(t *testing.T) {
	svc := NewConversationService(newFakeRepo())

	_, err := svc.GetConversationMessages(context.Background(), "userA", "", 0, 0)
	if err == nil {
		t.Fatal("sessionID kosong LOLOS")
	}
	if code := appErrCode(t, err); code != 400 {
		t.Errorf("code = %d, mau 400", code)
	}
}

func TestUserTanpaBisnis(t *testing.T) {
	svc := NewConversationService(newFakeRepo())

	if _, err := svc.ListConversations(context.Background(), "userTanpaBisnis"); err == nil {
		t.Error("user tanpa bisnis LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("code = %d, mau 404", code)
	}
}

// Limit dibatasi supaya satu request tidak bisa menarik seluruh riwayat.
func TestLimitDiclamp(t *testing.T) {
	repo := newFakeRepo()
	svc := NewConversationService(repo)
	ctx := context.Background()

	// Pesan juga diclamp.
	if _, err := svc.GetConversationMessages(ctx, "userA", "sesiA", 99999, 0); err != nil {
		t.Fatalf("get gagal: %v", err)
	}
	if repo.lastLimit != maxMessageLimit {
		t.Errorf("limit pesan = %d, mau diclamp ke %d", repo.lastLimit, maxMessageLimit)
	}
}
