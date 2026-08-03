package chat

type Response struct {
	Status  string      `json:"status"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

type WebHookRequest struct {
	Mode        string `json:"mode" validate:"required"`
	Challenge   string `json:"challenge" validate:"required"`
	VerifyToken string `json:"verify_token" validate:"required"`
}

type WebhookPayload struct {
	Object string  `json:"object"`
	Entry  []Entry `json:"entry"`
}

type Entry struct {
	ID      string   `json:"id"`
	Changes []Change `json:"changes"`
}

type Change struct {
	Value ChangeValue `json:"value"`
	Field string      `json:"field"`
}

type ChangeValue struct {
	MessagingProduct string    `json:"messaging_product"`
	Metadata         Metadata  `json:"metadata"`
	Contacts         []Contact `json:"contacts"`
	Messages         []Message `json:"messages"`
}

type Metadata struct {
	DisplayPhoneNumber string `json:"display_phone_number"`
	PhoneNumberID      string `json:"phone_number_id"`
}

type Contact struct {
	Profile Profile `json:"profile"`
	WaID    string  `json:"wa_id"`
}

type Profile struct {
	Name string `json:"name"`
}

type Message struct {
	From      string      `json:"from"`
	ID        string      `json:"id"`
	Timestamp string      `json:"timestamp"`
	Type      string      `json:"type"`
	Text      MessageText `json:"text"`
}

type MessageText struct {
	Body string `json:"body"`
}

// Struct untuk mengirim balasan pesan (API POST)
type WhatsAppMessagePayload struct {
	MessagingProduct string      `json:"messaging_product"`
	To               string      `json:"to"`
	Type             string      `json:"type"`
	Text             MessageText `json:"text"`
}

type CustomerRequest struct {
	BusinessID string `json:"business_id"`
	WaPhoneNumber string `json:"wa_phone_number"`
	Name string `json:"name"`
	Address string `json:"address"`
}


type SendMessage struct {
	To string `json:"to"`
	Msg string `json:"msg"`
}


