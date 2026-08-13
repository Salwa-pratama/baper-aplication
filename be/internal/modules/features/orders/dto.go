package orders

type OrderResponse struct {
	ID           string `json:"id"`
	CustomerName string `json:"customerName"`
	PackageName  string `json:"packageName"`
	Date         string `json:"date"`
	Amount       string `json:"amount"`
	Status       string `json:"status"`
	ChatID       string `json:"chatId"`
}
