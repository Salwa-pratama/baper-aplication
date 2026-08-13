package bot

import (
	"baper/internal/common/apperror"
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type BotController struct {
	service Service
}

func NewBotController(service Service) *BotController {
	return &BotController{service}
}

// currentUserID mengambil user_id yang sudah diverifikasi AuthMiddleware.
func currentUserID(ctx *fiber.Ctx) (string, error) {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok || userID == "" {
		return "", apperror.Unauthorized("unauthorized")
	}
	return userID, nil
}

// ToggleBotStatus godoc
// @Summary Toggle Bot Active Status
// @Description Enable/disable bot milik bisnis sendiri. Bot milik orang lain dijawab 404.
// @Tags Bot
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path string true "Bot ID"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/bots/{id}/toggle [patch]
func (c *BotController) ToggleBotStatus(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	botID := ctx.Params("id")
	if botID == "" {
		return res.HandleError(ctx, apperror.BadRequest("Bot ID wajib diisi"))
	}

	response, err := c.service.ToggleBotStatus(userID, botID)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengubah status bot", response))
}

// UpdateBotPrompt godoc
// @Summary Update Bot Prompt & API
// @Description Update Agent Prompt/API bot milik bisnis sendiri.
// @Tags Bot
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path string true "Bot ID"
// @Param body body UpdateBotPromptRequest true "Bot Configuration"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/bots/{id}/prompt [put]
func (c *BotController) UpdateBotPrompt(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	botID := ctx.Params("id")
	if botID == "" {
		return res.HandleError(ctx, apperror.BadRequest("Bot ID wajib diisi"))
	}

	var req UpdateBotPromptRequest
	if err := ctx.BodyParser(&req); err != nil {
		return res.HandleError(ctx, apperror.BadRequest("Format request tidak valid"))
	}

	response, err := c.service.UpdateBotPrompt(userID, botID, req)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil memperbarui konfigurasi bot", response))
}

// GetMyBot godoc
// @Summary Get My Bot
// @Description Mengambil data bot milik bisnis user yang login.
// @Tags Bot
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/bots/mine [get]
func (c *BotController) GetMyBot(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	response, err := c.service.GetMyBot(userID)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil data bot", response))
}
