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
		return ctx.Status(fiber.StatusInternalServerError).JSON(res.Error(err.Error()))
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("business registered", business))
}
