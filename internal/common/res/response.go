package res

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
