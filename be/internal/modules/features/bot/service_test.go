package bot

import (
	"errors"
	"testing"

	"baper/internal/common/apperror"
	"baper/internal/models"

	"gorm.io/gorm"
)

type fakeRepo struct {
	businessByUser map[string]*models.Business
	bots           map[string]*models.Bot
	toggled        []string
	promptUpdated  []string
}

func newFakeRepo() *fakeRepo {
	return &fakeRepo{
		businessByUser: map[string]*models.Business{
			"userA": {ID: "bizA", UserID: "userA"},
			"userB": {ID: "bizB", UserID: "userB"},
		},
		bots: map[string]*models.Bot{
			"botA": {ID: "botA", BusinessID: "bizA", Name: "Bot A", IsActive: true},
			"botB": {ID: "botB", BusinessID: "bizB", Name: "Bot B", IsActive: false},
		},
	}
}

func (f *fakeRepo) FindByID(id string) (*models.Bot, error) {
	b, ok := f.bots[id]
	if !ok {
		return nil, gorm.ErrRecordNotFound
	}
	return b, nil
}

func (f *fakeRepo) FindBusinessByUserID(userID string) (*models.Business, error) {
	b, ok := f.businessByUser[userID]
	if !ok {
		return nil, gorm.ErrRecordNotFound
	}
	return b, nil
}

func (f *fakeRepo) UpdateActiveStatus(id string, _ bool) error {
	f.toggled = append(f.toggled, id)
	return nil
}

func (f *fakeRepo) UpdateBotPrompt(id string, _ UpdateBotPromptRequest) error {
	f.promptUpdated = append(f.promptUpdated, id)
	return nil
}

func (f *fakeRepo) GetBotByBusinessID(businessID string) (*models.Bot, error) {
	for _, b := range f.bots {
		if b.BusinessID == businessID {
			return b, nil
		}
	}
	return nil, gorm.ErrRecordNotFound
}

func (f *fakeRepo) CreateBot(bot *models.Bot) error {
	f.bots[bot.ID] = bot
	return nil
}

func appErrCode(t *testing.T, err error) int {
	t.Helper()
	var ae *apperror.AppError
	if !errors.As(err, &ae) {
		t.Fatalf("error bukan *apperror.AppError: %#v", err)
	}
	return ae.Code
}

// Inti proteksi: userB tidak boleh mematikan bot atau mengganti prompt bot userA.
func TestUserBCannotTouchUserABot(t *testing.T) {
	repo := newFakeRepo()
	svc := NewBotService(repo)

	if _, err := svc.ToggleBotStatus("userB", "botA"); err == nil {
		t.Error("TOGGLE bot orang lain LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("toggle: code = %d, mau 404", code)
	}

	req := UpdateBotPromptRequest{AgentPrompt: "dibajak", AgentAPI: "http://evil"}
	if _, err := svc.UpdateBotPrompt("userB", "botA", req); err == nil {
		t.Error("UPDATE PROMPT bot orang lain LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("prompt: code = %d, mau 404", code)
	}

	if len(repo.toggled) != 0 {
		t.Errorf("toggle lolos ke repository: %v", repo.toggled)
	}
	if len(repo.promptUpdated) != 0 {
		t.Errorf("update prompt lolos ke repository: %v", repo.promptUpdated)
	}
}

func TestOwnerCanToggleAndUpdateOwnBot(t *testing.T) {
	repo := newFakeRepo()
	svc := NewBotService(repo)

	out, err := svc.ToggleBotStatus("userA", "botA")
	if err != nil {
		t.Fatalf("pemilik gagal toggle botnya sendiri: %v", err)
	}
	// botA awalnya aktif, jadi setelah toggle harus non-aktif.
	if active, ok := out["is_active"].(bool); !ok || active {
		t.Errorf("is_active = %v, mau false", out["is_active"])
	}
	if len(repo.toggled) != 1 || repo.toggled[0] != "botA" {
		t.Errorf("toggle tidak sampai repository: %v", repo.toggled)
	}

	if _, err := svc.UpdateBotPrompt("userA", "botA", UpdateBotPromptRequest{AgentPrompt: "ramah"}); err != nil {
		t.Fatalf("pemilik gagal update prompt: %v", err)
	}
	if len(repo.promptUpdated) != 1 {
		t.Errorf("update prompt tidak sampai repository: %v", repo.promptUpdated)
	}
}

func TestBotTidakDitemukan(t *testing.T) {
	svc := NewBotService(newFakeRepo())

	if _, err := svc.ToggleBotStatus("userA", "bot-yang-tidak-ada"); err == nil {
		t.Error("bot tidak ada tapi LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("code = %d, mau 404", code)
	}
}

func TestUserIDKosongDitolak(t *testing.T) {
	svc := NewBotService(newFakeRepo())

	if _, err := svc.ToggleBotStatus("", "botA"); err == nil {
		t.Error("userID kosong LOLOS")
	} else if code := appErrCode(t, err); code != 401 {
		t.Errorf("code = %d, mau 401", code)
	}
}

func TestUserTanpaBisnis(t *testing.T) {
	svc := NewBotService(newFakeRepo())

	if _, err := svc.ToggleBotStatus("userTanpaBisnis", "botA"); err == nil {
		t.Error("user tanpa bisnis LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("code = %d, mau 404", code)
	}
}
