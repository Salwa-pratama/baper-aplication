package chat

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"log"
	"mime"
	"mime/multipart"
	"net/http"
	"net/textproto"
	"os"
	"path/filepath"
	"runtime/debug"
	"strings"
	"time"

	"baper/internal/common/apperror"
	"baper/internal/models"
)

type Service interface {
	CekEndpoint(req WebHookRequest) (Response, error)
	ReceiveMessage(req WebhookPayload) (Response, error)
	SendMessage(to string, text string) error
	SendMediaMessage(to, mediaURL, mediaType, caption string) error
	UploadMedia(file multipart.File, filename string, contentType string) (string, error)
	SendMediaByID(to, mediaID, mediaType, caption string) error
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

	// Contacts bisa kosong pada beberapa jenis event webhook.
	// Tanpa cek panjang array, baris ini panic index out of range —
	// dan panic di dalam goroutine mematikan SELURUH proses server.
	customerName := ""
	if len(change.Value.Contacts) > 0 {
		customerName = change.Value.Contacts[0].Profile.Name
	}

	log.Printf("Pesan dari : %s", msg.From)
	log.Printf("Isi pesan : %s", msg.Text.Body)
	log.Printf("Nama Customer : %s", customerName)

	// 1. Dapatkan Bot dari PhoneNumberID (Nomor WhatsApp Business)
	bot, err := s.repo.FindBotByBusinessPhone(phone.PhoneNumberID)
	if err != nil {
		log.Printf("Gagal menemukan bot dengan nomor %s: %v", phone.PhoneNumberID, err)
		return Response{
			Status:  "error",
			Message: "Bot tidak ditemukan",
			Data:    nil,
		}, err
	}

	// 2. Dapatkan Customer dalam scope bisnis ini, jika belum ada maka Create.
	// Scope business_id penting: nomor WA yang sama bisa jadi customer di
	// dua bisnis berbeda, dan datanya tidak boleh tertukar.
	customer, errCust := s.repo.FindCustomerByPhoneAndBusiness(msg.From, bot.BusinessID)
	if errCust != nil {
		// Asumsi error karena tidak ketemu (gorm.ErrRecordNotFound)
		log.Printf("Customer %s belum ada di bisnis ini, membuat baru...", msg.From)
		customer = &models.Customer{
			BusinessID:    bot.BusinessID,
			WaPhoneNumber: msg.From,
			Name:          customerName,
		}
		if errCreate := s.repo.CreateCustomer(customer); errCreate != nil {
			log.Printf("Gagal membuat customer: %v", errCreate)
			return Response{
				Status:  "error",
				Message: "Gagal menyiapkan data customer",
				Data:    nil,
			}, errCreate
		}
	} else {
		log.Printf("Customer %s sudah ada (ID: %s)", msg.From, customer.ID)
	}

	// 3. Dapatkan atau Buat Chat Session
	session, errSess := s.repo.FindActiveChatSession(bot.ID, customer.ID)
	if errSess != nil {
		log.Printf("Tidak ada session aktif, membuat session baru...")
		session = &models.ChatSession{
			BotID:      bot.ID,
			CustomerID: customer.ID,
			Status:     "active",
			StartedAt:  time.Now(),
		}
		if errSession := s.repo.SaveChatSession(session); errSession != nil {
			log.Printf("Gagal membuat chat session: %v", errSession)
			return Response{
				Status:  "error",
				Message: "Gagal menyiapkan chat session",
				Data:    nil,
			}, errSession
		}
	} else {
		log.Printf("Menggunakan chat session aktif (ID: %s)", session.ID)
	}

	// 4. Simpan Pesan Masuk (Customer)
	incomingMsg := &models.Message{
		SessionID:  session.ID,
		SenderType: "customer",
		Content:    msg.Text.Body,
	}
	if errSave := s.repo.SaveMessage(incomingMsg); errSave != nil {
		log.Printf("Gagal menyimpan pesan masuk: %v", errSave)
	}

	// Cek apakah Bot aktif
	if !bot.IsActive {
		log.Printf("Bot %s sedang tidak aktif, mengabaikan AI reply", bot.Name)
		return Response{
			Status:  "ok",
			Message: "Pesan masuk disimpan, bot sedang tidak aktif",
			Data:    nil,
		}, nil
	}

	// 🔥 FAST-ACK: Jalankan AI dan pemrosesan order di Goroutine (Background)
	// Agar webhook langsung me-return 200 OK ke Meta dan mencegah Meta melakukan retry timeout.
	go func(botData *models.Bot, customerData *models.Customer, sessionData *models.ChatSession, msgBody string, senderPhone string) {
		// Panic di dalam goroutine TIDAK bisa ditangkap oleh Recover middleware
		// Fiber — tanpa recover di sini, satu panic mematikan seluruh proses.
		defer func() {
			if r := recover(); r != nil {
				log.Printf("PANIC saat memproses pesan di background: %v\n%s", r, debug.Stack())
			}
		}()

		// Fetch History
		historyMsgs, _ := s.repo.GetRecentMessages(sessionData.ID, 10)

		// Fetch Products
		products, _ := s.repo.GetProductsByBusinessID(botData.BusinessID)
		productsStr := ""
		for _, p := range products {
			productsStr += fmt.Sprintf("- ID: %s | %s | Harga: %.2f | Stok: %d\n", p.ID, p.Name, p.Price, p.Stock)
		}

		// 5. Build RAG Prompt context
		content, gagal := s.repo.GenerateContent(botData.AgentPrompt, historyMsgs, productsStr, msgBody)
		if gagal != nil {
			log.Println("AI tidak merespon: ", gagal)
			return // Jika gagal, jangan balas apa-apa
		}
		log.Print("AI Output: ", content)

		// Parse JSON output if exists
		cleanContent := content
		if jsonStart := strings.Index(content, "```json"); jsonStart != -1 {
			jsonEnd := strings.Index(content[jsonStart+7:], "```")
			if jsonEnd != -1 {
				jsonStr := content[jsonStart+7 : jsonStart+7+jsonEnd]

				var orderPayload AIOrderPayload

				if err := json.Unmarshal([]byte(jsonStr), &orderPayload); err == nil && orderPayload.IsOrderFinal {
					// Validasi keras terhadap output AI (lihat order_validation.go).
					orderItems, errValid := BuildOrderItems(products, orderPayload.Items)
					if errValid != nil {
						log.Printf("Order AI ditolak: %v", errValid)
					} else {
						sessIDStr := sessionData.ID
						newOrder := &models.Order{
							CustomerID: customerData.ID,
							BusinessID: botData.BusinessID,
							SessionID:  &sessIDStr,
							// TotalAmount dihitung ulang oleh repository dari harga DB.
							Status: models.OrderStatusUnpaid,
						}

						// SaveOrder mengunci stok, menolak jika kurang, lalu
						// mengurangi stok dalam satu transaksi.
						if err := s.repo.SaveOrder(newOrder, orderItems); err != nil {
							if errors.Is(err, ErrStokKurang) {
								log.Printf("Order dibatalkan, stok tidak cukup: %v", err)
							} else {
								log.Printf("Gagal menyimpan auto order: %v", err)
							}
						} else {
							log.Printf("Berhasil membuat auto order dengan ID: %s (total %.2f)", newOrder.ID, newOrder.TotalAmount)
						}
					}
				}

				// Remove the JSON part from the response sent to user
				cleanContent = strings.TrimSpace(content[:jsonStart] + content[jsonStart+7+jsonEnd+3:])
			}
		}

		// 🔥 Panggil fungsi SendMessage untuk mengirim balasan
		errSend := s.SendMessage(senderPhone, cleanContent)
		if errSend != nil {
			log.Println("Gagal balas pesan:", errSend)
		} else {
			// 5. Simpan Pesan Keluar (Bot AI)
			outgoingMsg := &models.Message{
				SessionID:  sessionData.ID,
				SenderType: "bot",
				Content:    cleanContent, // Simpan cleanContent (tanpa JSON) ke history database
			}
			if errSaveOut := s.repo.SaveMessage(outgoingMsg); errSaveOut != nil {
				log.Printf("Gagal menyimpan pesan bot: %v", errSaveOut)
			}
		}
	}(bot, customer, session, msg.Text.Body, msg.From)

	return Response{
		Status:  "ok",
		Message: "Ada pesan masuk, memproses di background",
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
	
	// Simpan pesan keluar (manual dari admin) ke database
	bot, errBot := s.repo.FindBotByBusinessPhone(phoneID)
	if errBot == nil {
		customer, errCust := s.repo.FindCustomerByPhoneAndBusiness(to, bot.BusinessID)
		if errCust == nil {
			session, errSess := s.repo.FindActiveChatSession(bot.ID, customer.ID)
			if errSess == nil {
				outgoingMsg := &models.Message{
					SessionID:  session.ID,
					SenderType: "bot", // bisa juga "admin", frontend hanya cek != "customer"
					Content:    text,
				}
				if errSave := s.repo.SaveMessage(outgoingMsg); errSave != nil {
					log.Printf("Gagal menyimpan pesan manual ke DB: %v", errSave)
				} else {
					log.Println("Pesan manual berhasil disimpan ke database")
				}
			} else {
				log.Printf("Gagal menemukan session aktif untuk disimpan: %v", errSess)
			}
		} else {
			log.Printf("Gagal menemukan customer untuk disimpan: %v", errCust)
		}
	} else {
		log.Printf("Gagal menemukan bot untuk disimpan: %v", errBot)
	}

	return nil
}

func (s *service) SendMediaMessage(to, mediaURL, mediaType, caption string) error {
	phoneID := os.Getenv("PHONE_NUMBER_ID")
	accessToken := os.Getenv("ACCESS_TOKEN")

	if phoneID == "" || accessToken == "" {
		return apperror.Internal("PHONE_NUMBER_ID atau ACCESS_TOKEN di .env kosong")
	}

	mediaBlock := &MediaBlock{
		Link:    mediaURL,
		Caption: caption,
	}

	payload := WhatsAppMediaPayload{
		MessagingProduct: "whatsapp",
		To:               to,
		Type:             mediaType,
	}

	switch mediaType {
	case "image":
		payload.Image = mediaBlock
	case "document":
		payload.Document = mediaBlock
	case "video":
		payload.Video = mediaBlock
	case "audio":
		payload.Audio = mediaBlock
	default:
		return apperror.BadRequest("Tipe media tidak didukung")
	}

	return s.sendWhatsAppPayload(payload, phoneID, accessToken)
}

func (s *service) UploadMedia(file multipart.File, filename string, contentType string) (string, error) {
	phoneID := os.Getenv("PHONE_NUMBER_ID")
	accessToken := os.Getenv("ACCESS_TOKEN")

	if phoneID == "" || accessToken == "" {
		return "", apperror.Internal("PHONE_NUMBER_ID atau ACCESS_TOKEN di .env kosong")
	}

	url := os.Getenv("ENDPOINT_SEND_MESSAGE") + phoneID + "/media"

	if contentType == "" || contentType == "application/octet-stream" {
		contentType = mime.TypeByExtension(filepath.Ext(filename))
		if contentType == "" {
			contentType = "application/octet-stream"
		}
	}
	log.Println("Uploading file:", filename, "dengan Content-Type:", contentType)

	var b bytes.Buffer
	w := multipart.NewWriter(&b)

	_ = w.WriteField("messaging_product", "whatsapp")

	h := make(textproto.MIMEHeader)
	h.Set("Content-Disposition", fmt.Sprintf(`form-data; name="file"; filename="%s"`, filename))
	h.Set("Content-Type", contentType)

	fw, err := w.CreatePart(h)
	if err != nil {
		return "", apperror.Internal("Gagal membuat form file")
	}
	if _, err = io.Copy(fw, file); err != nil {
		return "", apperror.Internal("Gagal copy isi file")
	}

	w.Close()

	req, err := http.NewRequest(http.MethodPost, url, &b)
	if err != nil {
		return "", apperror.Internal("Gagal membuat request")
	}

	req.Header.Set("Authorization", "Bearer "+accessToken)
	req.Header.Set("Content-Type", w.FormDataContentType())

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return "", apperror.Internal("Gagal request ke Meta API")
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return "", apperror.Internal("Gagal upload media ke Meta: " + string(bodyBytes))
	}

	var res UploadMediaResponse
	if err := json.NewDecoder(resp.Body).Decode(&res); err != nil {
		return "", apperror.Internal("Gagal decode response upload media")
	}

	return res.ID, nil
}

func (s *service) SendMediaByID(to, mediaID, mediaType, caption string) error {
	phoneID := os.Getenv("PHONE_NUMBER_ID")
	accessToken := os.Getenv("ACCESS_TOKEN")

	if phoneID == "" || accessToken == "" {
		return apperror.Internal("PHONE_NUMBER_ID atau ACCESS_TOKEN di .env kosong")
	}

	mediaBlock := &MediaBlock{
		ID:      mediaID,
		Caption: caption,
	}

	payload := WhatsAppMediaPayload{
		MessagingProduct: "whatsapp",
		To:               to,
		Type:             mediaType,
	}

	switch mediaType {
	case "image":
		payload.Image = mediaBlock
	case "document":
		payload.Document = mediaBlock
	case "video":
		payload.Video = mediaBlock
	case "audio":
		payload.Audio = mediaBlock
	default:
		return apperror.BadRequest("Tipe media tidak didukung")
	}

	return s.sendWhatsAppPayload(payload, phoneID, accessToken)
}

func (s *service) sendWhatsAppPayload(payload WhatsAppMediaPayload, phoneID, accessToken string) error {
	payloadBytes, err := json.Marshal(payload)
	if err != nil {
		return apperror.Internal("Gagal marshal payload")
	}

	url := os.Getenv("ENDPOINT_SEND_MESSAGE") + phoneID + "/messages"

	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(payloadBytes))
	if err != nil {
		return apperror.Internal("Gagal membuat request")
	}

	req.Header.Set("Authorization", "Bearer "+accessToken)
	req.Header.Set("Content-Type", "application/json")

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return apperror.Internal("Gagal request ke Meta API")
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return apperror.Internal("Gagal kirim media ke Meta: " + string(bodyBytes))
	}

	log.Println("Berhasil kirim media ke:", payload.To)
	return nil
}
