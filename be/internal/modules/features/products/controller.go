package products

import (
	"baper/internal/common/apperror"
	"baper/internal/common/res"

	"github.com/gofiber/fiber/v2"
)

type Controller struct {
	service Service
}

func NewProductController(service Service) *Controller {
	return &Controller{service}
}

// currentUserID mengambil user_id yang sudah diverifikasi AuthMiddleware.
func currentUserID(ctx *fiber.Ctx) (string, error) {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok || userID == "" {
		return "", apperror.Unauthorized("unauthorized")
	}
	return userID, nil
}

// CreateProduct godoc
// @Summary Create a Product
// @Description Create a new product. business_id diambil dari token, bukan dari body.
// @Tags Products
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param request body CreateProductRequest true "Product Data"
// @Success 201 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/products [post]
func (c *Controller) CreateProduct(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	var req CreateProductRequest
	if err := ctx.BodyParser(&req); err != nil {
		return res.HandleError(ctx, apperror.BadRequest("Format request tidak valid"))
	}

	product, err := c.service.CreateProduct(ctx.Context(), userID, req)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusCreated).JSON(res.Success("Produk berhasil dibuat", product))
}

// GetAllProducts godoc
// @Summary Get All Products
// @Description Get products milik bisnis user yang sedang login.
// @Tags Products
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/products [get]
func (c *Controller) GetAllProducts(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	products, err := c.service.GetAllProducts(ctx.Context(), userID)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil data produk", products))
}

// GetProductByID godoc
// @Summary Get Product by ID
// @Description Get detail produk. Produk milik bisnis lain dijawab 404.
// @Tags Products
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path string true "Product ID"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/products/{id} [get]
func (c *Controller) GetProductByID(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	id := ctx.Params("id")
	if id == "" {
		return res.HandleError(ctx, apperror.BadRequest("ID Produk wajib diisi"))
	}

	product, err := c.service.GetProductByID(ctx.Context(), userID, id)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil data produk", product))
}

// UpdateProduct godoc
// @Summary Update Product
// @Description Update produk milik bisnis sendiri.
// @Tags Products
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path string true "Product ID"
// @Param request body UpdateProductRequest true "Product Data"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/products/{id} [put]
func (c *Controller) UpdateProduct(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	id := ctx.Params("id")
	if id == "" {
		return res.HandleError(ctx, apperror.BadRequest("ID Produk wajib diisi"))
	}

	var req UpdateProductRequest
	if err := ctx.BodyParser(&req); err != nil {
		return res.HandleError(ctx, apperror.BadRequest("Format request tidak valid"))
	}

	product, err := c.service.UpdateProduct(ctx.Context(), userID, id, req)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Produk berhasil diupdate", product))
}

// DeleteProduct godoc
// @Summary Delete Product
// @Description Hapus produk milik bisnis sendiri.
// @Tags Products
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path string true "Product ID"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 401 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/products/{id} [delete]
func (c *Controller) DeleteProduct(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	id := ctx.Params("id")
	if id == "" {
		return res.HandleError(ctx, apperror.BadRequest("ID Produk wajib diisi"))
	}

	if err := c.service.DeleteProduct(ctx.Context(), userID, id); err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Produk berhasil dihapus", nil))
}
