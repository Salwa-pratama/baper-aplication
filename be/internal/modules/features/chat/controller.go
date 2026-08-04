package chat

import (
	"baper/internal/common/apperror"
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

func (c *ChatController) SendMediaMessage(ctx *fiber.Ctx) error {
	var payload SendMediaMessage
	if err := ctx.BodyParser(&payload); err != nil {
		return res.HandleError(ctx, apperror.BadRequest("Invalid Request payload"))
	}

	if payload.To == "" || payload.MediaType == "" || payload.MediaURL == "" {
		return res.HandleError(ctx, apperror.BadRequest("Field 'to', 'type', dan 'media_url' wajib diisi"))
	}

	err := c.service.SendMediaMessage(payload.To, payload.MediaURL, payload.MediaType, payload.Caption)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Pesan media terkirim", nil))
}

func (c *ChatController) SendMediaFile(ctx *fiber.Ctx) error {
	to := ctx.FormValue("to")
	mediaType := ctx.FormValue("type")
	caption := ctx.FormValue("caption")

	if to == "" || mediaType == "" {
		return res.HandleError(ctx, apperror.BadRequest("Field 'to' dan 'type' wajib diisi"))
	}

	fileHeader, err := ctx.FormFile("file")
	if err != nil {
		return res.HandleError(ctx, apperror.BadRequest("File tidak ditemukan dalam request"))
	}

	file, err := fileHeader.Open()
	if err != nil {
		return res.HandleError(ctx, apperror.Internal("Gagal membuka file upload"))
	}
	defer file.Close()

	mediaID, err := c.service.UploadMedia(file, fileHeader.Filename)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	err = c.service.SendMediaByID(to, mediaID, mediaType, caption)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Pesan media (file) terkirim", nil))
}
