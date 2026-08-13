package profile

type ProfileResponse struct {
	Name           string `json:"name"`
	Email          string `json:"email"`
	Phone          string `json:"phone"`
	Address        string `json:"address"`
	BusinessName   string `json:"business_name"`
	JoinedDate     string `json:"joined_date"`
	TotalOrders    int    `json:"total_orders"`
	TotalCustomers int    `json:"total_customers"`
}
