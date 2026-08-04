package auth

import "github.com/gofiber/fiber/v2"

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
// @Success 201 {object} map[string]interface{}
// @Failure 400 {object} map[string]interface{}
// @Router /api/auth/register [post]
func (c *RegisterController) Register(ctx *fiber.Ctx) error {
	var req RegisterRequest

	if err := ctx.BodyParser(&req); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "error",
			"message": "Format request tidak valid",
		})
	}

	// Basic validation
	if req.Email == "" || req.Password == "" || req.FirstName == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "error",
			"message": "Email, Password, dan FirstName wajib diisi",
		})
	}

	response, err := c.service.Register(req)
	if err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "error",
			"message": err.Error(),
		})
	}

	return ctx.Status(fiber.StatusCreated).JSON(fiber.Map{
		"status": "success",
		"message" : response,
	})
}
