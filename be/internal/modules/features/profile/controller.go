package profile

import (
	"baper/internal/common/apperror"
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type Controller struct {
	service Service
}

func NewProfileController(service Service) *Controller {
	return &Controller{service}
}

func currentUserID(ctx *fiber.Ctx) (string, error) {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok || userID == "" {
		return "", apperror.Unauthorized("unauthorized")
	}
	return userID, nil
}

// GetProfile godoc
// @Summary Dapatkan profil user dan bisnis saat ini
// @Description Mendapatkan data gabungan user dan bisnis untuk halaman profil
// @Tags Profile
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/profile [get]
func (c *Controller) GetProfile(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	data, err := c.service.GetProfile(ctx.Context(), userID)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil profil", data))
}
