# Gunakan versi stable, misal 1.23
FROM golang:1.25-alpine

WORKDIR /app

# git dibutuhkan buat go mod tidy, curl buat debug/healtcheck
RUN apk add --no-cache git curl

# Air buat hot-reload pas development
RUN go install github.com/air-verse/air@v1.61.7

# Swag buat generate dokumentasi API (Swagger)
RUN go install github.com/swaggo/swag/cmd/swag@latest

EXPOSE 8080

# Container jalan terus, siap di exec masuk lewat VS Code / terminal
CMD ["sleep", "infinity"]
