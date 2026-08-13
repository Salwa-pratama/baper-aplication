package orders

import (
	"baper/internal/common/apperror"
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type Controller struct {
	service Service
}

func NewOrderController(service Service) *Controller {
	return &Controller{service}
}

// currentUserID mengambil user_id yang sudah diverifikasi AuthMiddleware.
func currentUserID(ctx *fiber.Ctx) (string, error) {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok || userID == "" {
		return "", apperror.Unauthorized("user_id tidak ditemukan di token")
	}
	return userID, nil
}

// GetOrders godoc
// @Summary List Orders
// @Description Get list of orders for the authenticated business
// @Tags Orders
// @Accept json
// @Produce json
// @Success 200 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 500 {object} res.Response
// @Security BearerAuth
// @Router /api/orders [get]
func (c *Controller) GetOrders(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	orders, err := c.service.GetOrders(ctx.Context(), userID)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil daftar pesanan", orders))
}

// ConfirmPayment godoc
// @Summary Confirm Payment
// @Description Update order status to paid
// @Tags Orders
// @Accept json
// @Produce json
// @Param id path string true "Order ID"
// @Success 200 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 500 {object} res.Response
// @Security BearerAuth
// @Router /api/orders/{id}/status [patch]
func (c *Controller) ConfirmPayment(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	orderID := ctx.Params("id")
	if orderID == "" {
		return ctx.Status(fiber.StatusBadRequest).JSON(res.Error("Order ID harus diisi"))
	}

	err = c.service.ConfirmPayment(ctx.Context(), userID, orderID)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Status pesanan berhasil diupdate menjadi lunas", nil))
}
