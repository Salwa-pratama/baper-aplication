package middleware

import (
	"io"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"baper/internal/utils"

	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
)

func newApp(h ...fiber.Handler) *fiber.App {
	app := fiber.New()
	handlers := append(h, func(c *fiber.Ctx) error {
		uid, _ := c.Locals("user_id").(string)
		return c.JSON(fiber.Map{"user_id": uid})
	})
	app.Post("/protected", handlers...)
	return app
}

func doReq(t *testing.T, app *fiber.App, req *http.Request) (int, string) {
	t.Helper()
	resp, err := app.Test(req, 5000)
	if err != nil {
		t.Fatalf("app.Test error: %v", err)
	}
	defer resp.Body.Close()
	b, _ := io.ReadAll(resp.Body)
	return resp.StatusCode, string(b)
}

// ---------- AuthMiddleware ----------

func TestAuthMiddleware_NoHeader(t *testing.T) {
	t.Setenv("JWT_SECRET", "s3cret")
	app := newApp(AuthMiddleware())

	code, _ := doReq(t, app, httptest.NewRequest(http.MethodPost, "/protected", nil))
	if code != fiber.StatusUnauthorized {
		t.Errorf("tanpa header: status = %d, mau 401", code)
	}
}

func TestAuthMiddleware_ForgedToken(t *testing.T) {
	t.Setenv("JWT_SECRET", "s3cret-server")
	app := newApp(AuthMiddleware())

	tok := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"user_id": "ATTACKER",
		"exp":     time.Now().Add(time.Hour).Unix(),
	})
	forged, _ := tok.SignedString([]byte("secret-penyerang"))

	req := httptest.NewRequest(http.MethodPost, "/protected", nil)
	req.Header.Set("Authorization", "Bearer "+forged)

	code, body := doReq(t, app, req)
	if code != fiber.StatusUnauthorized {
		t.Errorf("token palsu: status = %d body = %s, mau 401", code, body)
	}
	if strings.Contains(body, "ATTACKER") {
		t.Errorf("user_id penyerang bocor ke handler: %s", body)
	}
}

func TestAuthMiddleware_ValidToken(t *testing.T) {
	t.Setenv("JWT_SECRET", "s3cret-server")
	app := newApp(AuthMiddleware())

	valid, err := utils.GenerateJWT("user-42", "a@b.com")
	if err != nil {
		t.Fatalf("GenerateJWT: %v", err)
	}

	req := httptest.NewRequest(http.MethodPost, "/protected", nil)
	req.Header.Set("Authorization", "Bearer "+valid)

	code, body := doReq(t, app, req)
	if code != fiber.StatusOK {
		t.Fatalf("token sah: status = %d body = %s, mau 200", code, body)
	}
	if !strings.Contains(body, "user-42") {
		t.Errorf("user_id tidak diteruskan ke handler: %s", body)
	}
}

func TestAuthMiddleware_MalformedScheme(t *testing.T) {
	t.Setenv("JWT_SECRET", "s3cret-server")
	app := newApp(AuthMiddleware())

	valid, _ := utils.GenerateJWT("user-42", "a@b.com")

	for _, h := range []string{valid, "Token " + valid, "Bearer", "Bearer a b"} {
		req := httptest.NewRequest(http.MethodPost, "/protected", nil)
		req.Header.Set("Authorization", h)
		if code, _ := doReq(t, app, req); code != fiber.StatusUnauthorized {
			t.Errorf("header %q: status = %d, mau 401", h, code)
		}
	}
}
