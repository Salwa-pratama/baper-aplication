package middleware

import (
	"baper/internal/common/apperror"
	"baper/internal/common/res"
	"baper/internal/utils"
	"strings"

	"github.com/gofiber/fiber/v2"
)

// AuthMiddleware memverifikasi Bearer token pada header Authorization.
// user_id hasil verifikasi disimpan di Locals("user_id").
func AuthMiddleware() fiber.Handler {
	return func(ctx *fiber.Ctx) error {
		authHeader := strings.TrimSpace(ctx.Get("Authorization"))
		if authHeader == "" {
			return res.HandleError(ctx, apperror.Unauthorized("missing authorization header"))
		}

		// Wajib format "Bearer <token>" (case-insensitive pada skema).
		parts := strings.Fields(authHeader)
		if len(parts) != 2 || !strings.EqualFold(parts[0], "Bearer") {
			return res.HandleError(ctx, apperror.Unauthorized("format authorization header harus 'Bearer <token>'"))
		}

		userID, err := utils.GetUserIDFromToken(parts[1])
		if err != nil {
			return res.HandleError(ctx, apperror.Unauthorized("invalid or expired token"))
		}

		// simpan userID ke Locals, biar bisa diakses di controller manapun
		ctx.Locals("user_id", userID)

		return ctx.Next() // lanjut ke handler berikutnya (controller)
	}
}
