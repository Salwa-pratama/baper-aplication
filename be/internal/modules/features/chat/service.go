package chat

import (
	"bytes"
	"encoding/json"
	"errors"
	"io"
	"log"
	"net/http"
	"os"
)

type Service interface {
	CekEndpoint(req WebHookRequest) (Response, error)
	ReceiveMessage(req WebhookPayload) (Response, error)
	SendMessage(to string, text string) error
}

type service struct {
	repo Repository
}

func NewChatService(repo Repository) Service {
	return &service{repo}
}

func (s *service) CekEndpoint(req WebHookRequest) (Response, error) {
	mode := req.Mode
	challenge := req.Challenge
	verify_token := req.VerifyToken

	// Dari environtment
	my_token := os.Getenv("VERIFY_TOKEN")

	log.Printf("mode : %s, challenge : %s, verify_token : %s", mode, challenge, verify_token)

	if mode == "subscribe" && verify_token == my_token {
		// Return Condition
		return Response{
			Status:  "success",
			Message: "Data Sesuai",
			Data:    challenge,
		}, nil
	}

	// Return Function
	return Response{
		Status:  "Failed",
		Message: "gagal verfikasi",
		Data:    nil,
	}, errors.New("verifikasi webhook gagal")
}

// endpoint untuk post menerima pesan
func (s *service) ReceiveMessage(req WebhookPayload) (Response, error) {
	// Guard caluse - cek payload ada isinya atau enggak
	if len(req.Entry) == 0 {
		return Response{
			Status:  "Not found",
			Message: "Data entry not found",
			Data:    nil,
		}, errors.New("Entry Kosong ")
	}

	entry := req.Entry[0]

	if len(entry.Changes) == 0 {
		return Response{
			Status:  "error",
			Message: "Changes kosong",
			Data:    nil,
		}, errors.New("Changes Kosong")
	}

	change := entry.Changes[0]

	if len(change.Value.Messages) == 0 {
		return Response{
			Status:  "ok",
			Message: "Tidak ada pesan baru (mungkin event status)",
			Data:    nil,
		}, nil
	}

	msg := change.Value.Messages[0]
	phone := change.Value.Metadata
	contact := change.Value.Contacts[0]

	log.Printf("Pesan dari : %s", msg.From)
	log.Printf("Isi pesan : %s",msg.Text.Body)
	log.Printf("Nama Customer : %s",contact.Profile.Name)

	// Generate content promt

	content, gagal := s.repo.GenerateContent(msg.Text.Body)
	log.Print(content)
	if gagal != nil {
		log.Println("AI tidak merespon %w : ", gagal)
	}

	// 🔥 Panggil fungsi SendMessage untuk mengirim balasan
	err := s.SendMessage(msg.From, content)
	if err != nil {
		log.Println("Gagal balas pesan:", err)
	}

	return Response{
		Status:  "ok",
		Message: "Ada pesan masuk",
		Data: map[string]string{
			"phone": phone.PhoneNumberID,
			"from":  msg.From,
			"body":  msg.Text.Body,
		},
	}, nil
}

func (s *service) SendMessage(to string, text string) error {
	phoneID := os.Getenv("PHONE_NUMBER_ID")
	accessToken := os.Getenv("ACCESS_TOKEN")

	if phoneID == "" || accessToken == "" {
		return errors.New("PHONE_NUMBER_ID atau WHATSAPP_ACCESS_TOKEN di .env kosong")
	}

	// 1. Siapkan payload JSON
	payload := WhatsAppMessagePayload{
		MessagingProduct: "whatsapp",
		To:               to,
		Type:             "text",
		Text: MessageText{
			Body: text,
		},
	}

	payloadBytes, err := json.Marshal(payload)
	if err != nil {
		return err
	}

	// 2. Tentukan URL endpoint Meta Graph API
	url := os.Getenv("ENDPOINT_SEND_MESSAGE") + phoneID + "/messages"

	// 3. Buat HTTP POST request
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(payloadBytes))
	if err != nil {
		return err
	}

	// 4. Set Header (Access Token & Content Type)
	req.Header.Set("Authorization", "Bearer "+accessToken)
	req.Header.Set("Content-Type", "application/json")

	// 5. Eksekusi Request
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	// Cek jika statusnya bukan 200 OK
	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return errors.New("gagal kirim pesan ke Meta: " + string(bodyBytes))
	}

	log.Println("Berhasil kirim pesan balasan ke:", to)
	return nil
}
