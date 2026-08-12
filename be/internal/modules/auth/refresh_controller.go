package auth

import (
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type RefreshController struct {
	service Service
}

func NewRefreshController(service Service) *RefreshController {
	return &RefreshController{service}
}

// RefreshToken godoc
// @Summary Refresh Access Token
// @Description Tukar refresh token yang masih berlaku dengan access token baru.
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body RefreshTokenRequest true "Refresh Token"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Router /api/auth/refresh [post]
func (c *RefreshController) RefreshToken(ctx *fiber.Ctx) error {
	var req RefreshTokenRequest

	if err := ctx.BodyParser(&req); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Format request tidak valid"))
	}

	if req.RefreshToken == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Refresh token wajib diisi"))
	}

	data, err := c.service.RefreshToken(req)
	if err != nil {
		return ctx.Status(fiber.StatusUnauthorized).JSON(res.Error(err.Error()))
	}

	return ctx.JSON(res.Success("Token berhasil diperbarui", data))
}
