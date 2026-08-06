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

// GetMonthlyRecap godoc
// @Summary Get Monthly Recap
// @Description Get monthly recap of total revenue and orders
// @Tags Business
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param year query int true "Year (e.g. 2026)"
// @Param month query int true "Month (1-12)"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Router /api/business/recap/monthly [get]
func (c *BusinessController) GetMonthlyRecap(ctx *fiber.Ctx) error {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok {
		return ctx.Status(fiber.StatusUnauthorized).JSON(res.Error("unauthorized"))
	}

	year := ctx.QueryInt("year", 0)
	month := ctx.QueryInt("month", 0)

	if year == 0 || month == 0 || month < 1 || month > 12 {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Parameter year dan month tidak valid"))
	}

	recap, err := c.service.GetMonthlyRecap(ctx.Context(), userID, year, month)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil rekap", recap))
}

// ExportMonthlyRecap godoc
// @Summary Export Monthly Recap to CSV
// @Description Download monthly recap in CSV format
// @Tags Business
// @Produce text/csv
// @Security BearerAuth
// @Param year query int true "Year (e.g. 2026)"
// @Param month query int true "Month (1-12)"
// @Success 200 {file} file
// @Failure 400 {object} res.Response
// @Router /api/business/recap/monthly/export [get]
func (c *BusinessController) ExportMonthlyRecap(ctx *fiber.Ctx) error {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok {
		return ctx.Status(fiber.StatusUnauthorized).JSON(res.Error("unauthorized"))
	}

	year := ctx.QueryInt("year", 0)
	month := ctx.QueryInt("month", 0)

	if year == 0 || month == 0 || month < 1 || month > 12 {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Parameter year dan month tidak valid"))
	}

	csvData, err := c.service.ExportMonthlyRecap(ctx.Context(), userID, year, month)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	ctx.Set("Content-Type", "text/csv")
	ctx.Set("Content-Disposition", "attachment; filename=\"rekap_bulanan.csv\"")
	return ctx.SendString(csvData)
}
