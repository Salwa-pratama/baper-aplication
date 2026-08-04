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

// CekVerification godoc
// @Summary Webhook Verification
// @Description Verify webhook endpoint for Meta WhatsApp API
// @Tags Webhook
// @Accept json
// @Produce plain
// @Param hub.mode query string true "Mode"
// @Param hub.challenge query string true "Challenge"
// @Param hub.verify_token query string true "Verify Token"
// @Success 200 {string} string "challenge"
// @Failure 403 {object} res.Response
// @Router /api/webhook/ [get]
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

// ReceiveMessage godoc
// @Summary Receive Webhook
// @Description Receive incoming messages from Meta WhatsApp API
// @Tags Webhook
// @Accept json
// @Produce json
// @Param payload body WebhookPayload true "Webhook Payload"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/webhook/verify [post]
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

// SendMessage godoc
// @Summary Send Text Message
// @Description Send a text message via WhatsApp
// @Tags Webhook
// @Accept json
// @Produce json
// @Param request body SendMessage true "Message Data"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 403 {object} res.Response
// @Router /api/webhook/send-message [post]
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

// SendMediaMessage godoc
// @Summary Send Media via URL
// @Description Send a media message (image/document/video/audio) using a public URL
// @Tags Webhook
// @Accept json
// @Produce json
// @Param request body SendMediaMessage true "Media Message Data"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Router /api/webhook/send-media [post]
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

// SendMediaFile godoc
// @Summary Send Media via File Upload
// @Description Upload a local file and send it as a media message
// @Tags Webhook
// @Accept multipart/form-data
// @Produce json
// @Param to formData string true "Destination Phone Number (e.g. 62812...)"
// @Param type formData string true "Media Type (image, document, video, audio)"
// @Param caption formData string false "Optional Caption"
// @Param file formData file true "File to upload"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/webhook/upload-media [post]
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
	contentType := fileHeader.Header.Get("Content-Type")

	file, err := fileHeader.Open()
	if err != nil {
		return res.HandleError(ctx, apperror.Internal("Gagal membuka file upload"))
	}
	defer file.Close()

	mediaID, err := c.service.UploadMedia(file, fileHeader.Filename, contentType)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	err = c.service.SendMediaByID(to, mediaID, mediaType, caption)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Pesan media (file) terkirim", nil))
}
