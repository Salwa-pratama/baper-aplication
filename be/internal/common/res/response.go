package res

import (
	"baper/internal/common/apperror"
	"errors"

	"github.com/gofiber/fiber/v2"
)

type Response struct {
	Status  bool        `json:"status"`
	Message string      `json:"message"`
	Data    interface{} `json:"data,omitempty"`
}

func Success(message string, data interface{}) Response {
	return Response{
		Status:  true,
		Message: message,
		Data:    data,
	}
}

func Error(message string) Response {
	return Response{
		Status:  false,
		Message: message,
		Data:    nil,
	}
}

// HandleError — otomatis nge-detect apakah error itu AppError (custom) atau error biasa
// terus otomatis kirim response dengan status code yang sesuai
func HandleError(ctx *fiber.Ctx, err error) error {
	var appErr *apperror.AppError

	if errors.As(err, &appErr) {
		return ctx.Status(appErr.Code).JSON(Error(appErr.Message))
	}

	// kalau bukan AppError (error nggak terduga/bug), fallback ke 500
	// dan JANGAN expose pesan error asli ke client (bisa bocorin detail internal)
	return ctx.Status(fiber.StatusInternalServerError).JSON(Error("terjadi kesalahan pada server"))
}
