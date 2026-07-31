package middleware

import (
	"baper/internal/utils"
	"strings"

	"github.com/gofiber/fiber/v2"
)

func AuthMiddleware() fiber.Handler {
	return func(ctx *fiber.Ctx) error {
		authHeader := ctx.Get("Authorization")
		if authHeader == "" {
			return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"status":  false,
				"message": "missing authorization header",
			})
		}

		// biasanya formatnya "Bearer <token>"
		tokenString := strings.TrimPrefix(authHeader, "Bearer ")

		userID, err := utils.GetUserIDFromToken(tokenString)
		if err != nil {
			return ctx.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"status":  false,
				"message": "invalid or expired token",
			})
		}

		// simpan userID ke Locals, biar bisa diakses di controller manapun
		ctx.Locals("user_id", userID)

		return ctx.Next() // lanjut ke handler berikutnya (controller)
	}
}
