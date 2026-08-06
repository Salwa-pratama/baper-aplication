package auth

import (
	"baper/internal/common/res"
	"github.com/gofiber/fiber/v2"
)

type LoginController struct {
	service Service
}

func NewLoginController(service Service) *LoginController {
	return &LoginController{service}
}

// Login godoc
// @Summary Login User
// @Description Authenticate a user and return JWT tokens
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body LoginRequest true "Login Credentials"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Router /api/auth/login [post]
func (c *LoginController) Login(ctx *fiber.Ctx) error {
	var req LoginRequest

	if err := ctx.BodyParser(&req); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Format request tidak valid"))
	}

	if req.Email == "" || req.Password == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Email dan Password wajib diisi"))
	}

	response, err := c.service.SignIn(req)
	if err != nil {
		return ctx.Status(fiber.StatusUnauthorized).JSON(res.Error(err.Error()))
	}

	return ctx.JSON(res.Success("Login Successfully", response.Data))
}
