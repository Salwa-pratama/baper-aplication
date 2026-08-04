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

// CreateProduct godoc
// @Summary Create a Product
// @Description Create a new product for a business
// @Tags Products
// @Accept json
// @Produce json
// @Param request body CreateProductRequest true "Product Data"
// @Success 201 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/products [post]
func (c *Controller) CreateProduct(ctx *fiber.Ctx) error {
	var req CreateProductRequest
	if err := ctx.BodyParser(&req); err != nil {
		return res.HandleError(ctx, apperror.BadRequest("Format request tidak valid"))
	}

	product, err := c.service.CreateProduct(ctx.Context(), req)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusCreated).JSON(res.Success("Produk berhasil dibuat", product))
}

// GetAllProducts godoc
// @Summary Get All Products
// @Description Get a list of all products, optionally filtered by business ID
// @Tags Products
// @Accept json
// @Produce json
// @Param business_id query string false "Business ID"
// @Success 200 {object} res.Response
// @Failure 500 {object} res.Response
// @Router /api/products [get]
func (c *Controller) GetAllProducts(ctx *fiber.Ctx) error {
	businessID := ctx.Query("business_id")
	
	products, err := c.service.GetAllProducts(ctx.Context(), businessID)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil data produk", products))
}

// GetProductByID godoc
// @Summary Get Product by ID
// @Description Get detailed information about a specific product
// @Tags Products
// @Accept json
// @Produce json
// @Param id path string true "Product ID"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/products/{id} [get]
func (c *Controller) GetProductByID(ctx *fiber.Ctx) error {
	id := ctx.Params("id")
	if id == "" {
		return res.HandleError(ctx, apperror.BadRequest("ID Produk wajib diisi"))
	}

	product, err := c.service.GetProductByID(ctx.Context(), id)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Berhasil mengambil data produk", product))
}

// UpdateProduct godoc
// @Summary Update Product
// @Description Update an existing product's details
// @Tags Products
// @Accept json
// @Produce json
// @Param id path string true "Product ID"
// @Param request body UpdateProductRequest true "Product Data"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/products/{id} [put]
func (c *Controller) UpdateProduct(ctx *fiber.Ctx) error {
	id := ctx.Params("id")
	if id == "" {
		return res.HandleError(ctx, apperror.BadRequest("ID Produk wajib diisi"))
	}

	var req UpdateProductRequest
	if err := ctx.BodyParser(&req); err != nil {
		return res.HandleError(ctx, apperror.BadRequest("Format request tidak valid"))
	}

	product, err := c.service.UpdateProduct(ctx.Context(), id, req)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Produk berhasil diupdate", product))
}

// DeleteProduct godoc
// @Summary Delete Product
// @Description Delete a product by its ID
// @Tags Products
// @Accept json
// @Produce json
// @Param id path string true "Product ID"
// @Success 200 {object} res.Response
// @Failure 400 {object} res.Response
// @Failure 404 {object} res.Response
// @Router /api/products/{id} [delete]
func (c *Controller) DeleteProduct(ctx *fiber.Ctx) error {
	id := ctx.Params("id")
	if id == "" {
		return res.HandleError(ctx, apperror.BadRequest("ID Produk wajib diisi"))
	}

	err := c.service.DeleteProduct(ctx.Context(), id)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusOK).JSON(res.Success("Produk berhasil dihapus", nil))
}
