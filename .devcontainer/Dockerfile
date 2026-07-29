# Gunakan versi stable, misal 1.23
FROM golang:1.24-alpine

WORKDIR /app

# git dibutuhkan buat go mod tidy, curl buat debug/healtcheck
RUN apk add --no-cache git curl

# Air buat hot-reload pas development
RUN go install github.com/air-verse/air@v1.61.7

EXPOSE 8080

# Container jalan terus, siap di exec masuk lewat VS Code / terminal
CMD ["sleep", "infinity"]
