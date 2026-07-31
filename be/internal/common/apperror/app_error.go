package apperror

import "net/http"

type AppError struct {
	Code    int    // HTTP status code
	Message string
}

func (e *AppError) Error() string {
	return e.Message
}

// Constructor buat berbagai jenis error umum

func BadRequest(message string) *AppError {
	return &AppError{Code: http.StatusBadRequest, Message: message} // 400
}

func Unauthorized(message string) *AppError {
	return &AppError{Code: http.StatusUnauthorized, Message: message} // 401
}

func Forbidden(message string) *AppError {
	return &AppError{Code: http.StatusForbidden, Message: message} // 403
}

func NotFound(message string) *AppError {
	return &AppError{Code: http.StatusNotFound, Message: message} // 404
}

func Conflict(message string) *AppError {
	return &AppError{Code: http.StatusConflict, Message: message} // 409
}

func Internal(message string) *AppError {
	return &AppError{Code: http.StatusInternalServerError, Message: message} // 500
}
