package auth

import (
	"baper/internal/common/res"
	"github.com/gofiber/fiber/v2"
)

type RegisterController struct {
	service Service
}

func NewRegisterController(service Service) *RegisterController {
	return &RegisterController{service}
}

// Register godoc
// @Summary Register New User
// @Description Register a new user into the system
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body RegisterRequest true "Registration Data"
// @Success 201 {object} res.Response
// @Failure 400 {object} res.Response
// @Router /api/auth/register [post]
func (c *RegisterController) Register(ctx *fiber.Ctx) error {
	var req RegisterRequest

	if err := ctx.BodyParser(&req); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Format request tidak valid"))
	}

	// Basic validation
	if req.Email == "" || req.Password == "" || req.FirstName == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Email, Password, dan FirstName wajib diisi"))
	}

	response, err := c.service.Register(req)
	if err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error(err.Error()))
	}

	return ctx.Status(fiber.StatusCreated).JSON(res.Success("Register successfully", response))
}
