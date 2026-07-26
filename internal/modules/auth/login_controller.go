package auth

import "github.com/gofiber/fiber/v2"

type LoginController struct {
	service Service
}

func NewLoginController(service Service) *LoginController {
	return &LoginController{service}
}

func (c *LoginController) Login(ctx *fiber.Ctx) error {
	var req LoginRequest

	if err := ctx.BodyParser(&req); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "error",
			"message": "Format request tidak valid",
		})
	}

	if req.Email == "" || req.Password == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "error",
			"message": "Email dan Password wajib diisi",
		})
	}

	response, err := c.service.SignIn(req)
	if err != nil {
		return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"status":  "error",
			"message": err.Error(),
		})
	}

	return ctx.JSON(fiber.Map{
		"status": "success",
		"data":   response,
	})
}
