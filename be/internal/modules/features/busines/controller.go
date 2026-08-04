package busines

import (
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)


type BusinessController struct {
	service Service
}


func NewBusinessController(service Service) *BusinessController {
	return &BusinessController{service}
}

// RegisterBusiness godoc
// @Summary Register a Business
// @Description Register a new business for the authenticated user
// @Tags Business
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param request body RegisterBusinessRequest true "Business Data"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Router /api/business/register [post]
func (c *BusinessController) RegisterBusiness(ctx *fiber.Ctx) error {
	var req RegisterBusinessRequest
	if err := ctx.BodyParser(&req); err != nil {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("invalid request"))
	}

	// ambil userID dari locals (yang udah di-set middleware auth)
	userID, ok := ctx.Locals("user_id").(string)
	if !ok {
		return ctx.Status(fiber.StatusUnauthorized).JSON(res.Error("unauthorized"))
	}

	business, err := c.service.RegisterBusiness(ctx.Context(), userID, req)
	if err != nil {
		return res.HandleError(ctx, err) // <-- ganti ini, biar baca Code dari AppError
	}


	return ctx.Status(fiber.StatusOK).JSON(res.Success("business registered", business))
}
