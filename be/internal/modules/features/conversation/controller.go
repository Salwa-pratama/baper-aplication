package conversation

import (
	"strconv"

	"baper/internal/common/apperror"
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type Controller struct {
	service Service
}

func NewConversationController(service Service) *Controller {
	return &Controller{service}
}

// currentUserID mengambil user_id yang sudah diverifikasi AuthMiddleware.
func currentUserID(ctx *fiber.Ctx) (string, error) {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok || userID == "" {
		return "", apperror.Unauthorized("unauthorized")
	}
	return userID, nil
}

// queryInt membaca query param angka; nilai tidak valid diabaikan (pakai default).
func queryInt(ctx *fiber.Ctx, key string) int {
	raw := ctx.Query(key)
	if raw == "" {
		return 0
	}
	n, err := strconv.Atoi(raw)
	if err != nil {
		return 0
	}
	return n
}

// ListConversations godoc
// @Summary Daftar percakapan (chat list)
// @Description Daftar semua chat session milik bisnis user yang sedang login, beserta nama customer, nomor WhatsApp, dan preview pesan terakhir. Dipakai untuk layar daftar chat. Diurutkan dari aktivitas terbaru.
// @Tags Conversations
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} res.Response{data=[]ConversationResponse}
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/conversations [get]
func (c *Controller) ListConversations(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	list, err := c.service.ListConversations(
		ctx.Context(),
		userID,
	)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil daftar percakapan", list))
}

// GetConversationMessages godoc
// @Summary Pesan dalam satu percakapan
// @Description Ambil seluruh pesan dari satu chat session beserta identitas customer-nya. Endpoint ini yang dipanggil ketika card customer di daftar chat ditekan. Percakapan milik bisnis lain dijawab 404.
// @Tags Conversations
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path string true "Chat Session ID"
// @Param limit query int false "Jumlah maksimum pesan (default 100, maks 200)"
// @Param offset query int false "Offset paging (default 0)"
// @Success 200 {object} res.Response{data=ConversationDetailResponse}
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/conversations/{id}/messages [get]
func (c *Controller) GetConversationMessages(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	sessionID := ctx.Params("id")
	if sessionID == "" {
		return res.HandleError(ctx, apperror.BadRequest("ID sesi wajib diisi"))
	}

	detail, err := c.service.GetConversationMessages(
		ctx.Context(),
		userID,
		sessionID,
		queryInt(ctx, "limit"),
		queryInt(ctx, "offset"),
	)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil pesan percakapan", detail))
}
