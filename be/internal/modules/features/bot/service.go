package bot

import (
	"baper/internal/common/apperror"
	"baper/internal/models"
	"errors"

	"gorm.io/gorm"
)

type Service interface {
	ToggleBotStatus(userID string, botID string) (map[string]interface{}, error)
	UpdateBotPrompt(userID string, botID string, req UpdateBotPromptRequest) (map[string]interface{}, error)
	GetMyBot(userID string) (map[string]interface{}, error)
}

type service struct {
	repo Repository
}

func NewBotService(repo Repository) Service {
	return &service{repo}
}

// findOwnedBot mengambil bot HANYA jika bot itu milik bisnis user yang login.
// Bot orang lain dijawab 404 supaya tidak membocorkan keberadaannya.
func (s *service) findOwnedBot(userID, botID string) (*models.Bot, error) {
	if userID == "" {
		return nil, apperror.Unauthorized("user tidak dikenali")
	}

	business, err := s.repo.FindBusinessByUserID(userID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apperror.NotFound("Bisnis tidak ditemukan untuk user ini")
		}
		return nil, apperror.Internal("Gagal mengambil data bisnis")
	}

	bot, err := s.repo.FindByID(botID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apperror.NotFound("Bot tidak ditemukan")
		}
		return nil, apperror.Internal("Gagal mengambil data bot")
	}

	if bot.BusinessID != business.ID {
		return nil, apperror.NotFound("Bot tidak ditemukan")
	}

	return bot, nil
}

func (s *service) ToggleBotStatus(userID string, botID string) (map[string]interface{}, error) {
	bot, err := s.findOwnedBot(userID, botID)
	if err != nil {
		return nil, err
	}

	// Toggle status
	newStatus := !bot.IsActive
	if err := s.repo.UpdateActiveStatus(bot.ID, newStatus); err != nil {
		return nil, apperror.Internal("Gagal memperbarui status bot")
	}

	return map[string]interface{}{
		"id":        bot.ID,
		"is_active": newStatus,
	}, nil
}

func (s *service) UpdateBotPrompt(userID string, botID string, req UpdateBotPromptRequest) (map[string]interface{}, error) {
	bot, err := s.findOwnedBot(userID, botID)
	if err != nil {
		return nil, err
	}

	if err := s.repo.UpdateBotPrompt(bot.ID, req); err != nil {
		return nil, apperror.Internal("Gagal memperbarui prompt bot")
	}

	return map[string]interface{}{
		"id":           bot.ID,
		"agent_prompt": req.AgentPrompt,
		"agent_api":    req.AgentAPI,
	}, nil
}

func (s *service) GetMyBot(userID string) (map[string]interface{}, error) {
	if userID == "" {
		return nil, apperror.Unauthorized("user tidak dikenali")
	}

	business, err := s.repo.FindBusinessByUserID(userID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apperror.NotFound("Bisnis tidak ditemukan untuk user ini")
		}
		return nil, apperror.Internal("Gagal mengambil data bisnis")
	}

	bot, err := s.repo.GetBotByBusinessID(business.ID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			// Create bot if not exists
			newBot := models.Bot{
				BusinessID: business.ID,
				Name:       "Bot " + business.Name,
				IsActive:   true,
			}
			if err := s.repo.CreateBot(&newBot); err != nil {
				return nil, apperror.Internal("Gagal membuat bot otomatis")
			}
			bot = &newBot
		} else {
			return nil, apperror.Internal("Gagal mengambil data bot")
		}
	}

	return map[string]interface{}{
		"id":           bot.ID,
		"name":         bot.Name,
		"is_active":    bot.IsActive,
		"agent_prompt": bot.AgentPrompt,
		"agent_api":    bot.AgentAPI,
	}, nil
}
