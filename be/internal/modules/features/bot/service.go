package bot

import (
	"errors"
)

type Service interface {
	ToggleBotStatus(botID string) (map[string]interface{}, error)
	UpdateBotPrompt(botID string, req UpdateBotPromptRequest) (map[string]interface{}, error)
}

type service struct {
	repo Repository
}

func NewBotService(repo Repository) Service {
	return &service{repo}
}

func (s *service) ToggleBotStatus(botID string) (map[string]interface{}, error) {
	bot, err := s.repo.FindByID(botID)
	if err != nil || bot == nil {
		return nil, errors.New("bot tidak ditemukan")
	}

	// Toggle status
	newStatus := !bot.IsActive
	err = s.repo.UpdateActiveStatus(botID, newStatus)
	if err != nil {
		return nil, errors.New("gagal memperbarui status bot")
	}

	return map[string]interface{}{
		"id":        bot.ID,
		"is_active": newStatus,
	}, nil
}

func (s *service) UpdateBotPrompt(botID string, req UpdateBotPromptRequest) (map[string]interface{}, error) {
	bot, err := s.repo.FindByID(botID)
	if err != nil || bot == nil {
		return nil, errors.New("bot tidak ditemukan")
	}

	err = s.repo.UpdateBotPrompt(botID, req)
	if err != nil {
		return nil, errors.New("gagal memperbarui prompt bot")
	}

	return map[string]interface{}{
		"id":           bot.ID,
		"agent_prompt": req.AgentPrompt,
		"agent_api":    req.AgentAPI,
	}, nil
}
