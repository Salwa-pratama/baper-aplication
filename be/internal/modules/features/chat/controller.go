package chat

import (
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type ChatController struct {
	service Service
}

func NewChatController(service Service) *ChatController {
	return &ChatController{service}
}

func (c *ChatController) CekVerification(ctx *fiber.Ctx) error {
	req := WebHookRequest{
		Mode:        ctx.Query("hub.mode"),
		Challenge:   ctx.Query("hub.challenge"),
		VerifyToken: ctx.Query("hub.verify_token"),
	}

	resp, err := c.service.CekEndpoint(req)
	if err != nil {
		return ctx.Status(fiber.StatusForbidden).JSON(resp)
	}

	// Meta expect plain text challenge, BUKAN JSON!
	return ctx.Status(fiber.StatusOK).SendString(resp.Data.(string))
}

func (c *ChatController) ReceiveMessage(ctx *fiber.Ctx) error {
	var payload WebhookPayload

	if err := ctx.BodyParser(&payload); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "error",
			"message": "Invalid payload",
		})
	}

	resp, err := c.service.ReceiveMessage(payload)
	if err != nil {
		return ctx.Status(fiber.StatusInternalServerError).JSON(resp)
	}

	return ctx.Status(fiber.StatusOK).JSON(resp)
}

func (c *ChatController) SendMessage(ctx *fiber.Ctx) error {
	var payload SendMessage
	if err := ctx.BodyParser(&payload); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Invalid Request"))
	}

	err := c.service.SendMessage(payload.To, payload.Msg)

	if err != nil {
		return ctx.Status(fiber.StatusForbidden).JSON(res.Error("Gagal mengirim pesan"))
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Pesan Anda terkirim",nil))
}
