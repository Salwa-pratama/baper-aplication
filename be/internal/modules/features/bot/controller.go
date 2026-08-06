package bot

import (
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type BotController struct {
	service Service
}

func NewBotController(service Service) *BotController {
	return &BotController{service}
}

// ToggleBotStatus godoc
// @Summary Toggle Bot Active Status
// @Description Enable or disable the AI bot for a specific business
// @Tags Bot
// @Accept json
// @Produce json
// @Param id path string true "Bot ID"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/bots/{id}/toggle [patch]
func (c *BotController) ToggleBotStatus(ctx *fiber.Ctx) error {
	botID := ctx.Params("id")
	if botID == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Bot ID wajib diisi"))
	}

	response, err := c.service.ToggleBotStatus(botID)
	if err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error(err.Error()))
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengubah status bot", response))
}

// UpdateBotPrompt godoc
// @Summary Update Bot Prompt & API
// @Description Update the AI Agent Prompt and API configuration for the bot
// @Tags Bot
// @Accept json
// @Produce json
// @Param id path string true "Bot ID"
// @Param body body UpdateBotPromptRequest true "Bot Configuration"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/bots/{id}/prompt [put]
func (c *BotController) UpdateBotPrompt(ctx *fiber.Ctx) error {
	botID := ctx.Params("id")
	if botID == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Bot ID wajib diisi"))
	}

	var req UpdateBotPromptRequest
	if err := ctx.BodyParser(&req); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Format request tidak valid"))
	}

	response, err := c.service.UpdateBotPrompt(botID, req)
	if err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error(err.Error()))
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil memperbarui konfigurasi bot", response))
}
