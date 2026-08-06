package bot

import (
	"baper/internal/models"

	"gorm.io/gorm"
)

type Repository interface {
	FindByID(id string) (*models.Bot, error)
	UpdateActiveStatus(id string, isActive bool) error
	UpdateBotPrompt(id string, req UpdateBotPromptRequest) error
}

type repository struct {
	db *gorm.DB
}

func NewBotRepository(db *gorm.DB) Repository {
	return &repository{db}
}

func (r *repository) FindByID(id string) (*models.Bot, error) {
	var bot models.Bot
	err := r.db.Where("id = ?", id).First(&bot).Error
	if err != nil {
		return nil, err
	}
	return &bot, nil
}

func (r *repository) UpdateActiveStatus(id string, isActive bool) error {
	return r.db.Model(&models.Bot{}).Where("id = ?", id).Update("is_active", isActive).Error
}

func (r *repository) UpdateBotPrompt(id string, req UpdateBotPromptRequest) error {
	return r.db.Model(&models.Bot{}).Where("id = ?", id).Updates(map[string]interface{}{
		"agent_prompt": req.AgentPrompt,
		"agent_api":    req.AgentAPI,
	}).Error
}
