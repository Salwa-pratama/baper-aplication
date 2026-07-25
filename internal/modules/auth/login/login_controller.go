package login

import "github.com/gofiber/fiber/v2"



type Controller struct {
	service Service
}


func NewLoginController(service Service) *Controller {
	return &Controller{service}
}

// Testing login api
func (c *Controller) Login(ctx *fiber.Ctx) error {
	sayHay , err := c.service.SignIn()


	if err != nil {
		return ctx.Status(fiber.StatusInternalServerError).JSON(fiber.Map {
			"status" : "error",
			"message" : "Gagal mengangkap endpoint",
		})
	}

	return ctx.JSON(fiber.Map{
		"status" : "ok",
		"message" : sayHay,
	})
}
