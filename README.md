# Baper (Bantu Pesan dan Rekap)

## 📖 Deskripsi Aplikasi
**Baper** adalah sistem yang dirancang khusus untuk membantu UMKM dalam mengelola pesanan dan merekap penjualan secara lebih efisien. Aplikasi ini menggunakan integrasi bot WhatsApp yang ditenagai oleh AI (Google Gemini) untuk berinteraksi dengan pelanggan secara otomatis dan mencatat pesanan. Sementara itu, pemilik usaha dapat memantau pesanan yang masuk dan merekapnya secara _real-time_ melalui aplikasi Android pendamping.

Aplikasi ini dibagi menjadi dua bagian dalam satu repositori (*monorepo*):
- `be/` : Backend API berbasis Go
- `baper-andoid/` : Frontend aplikasi mobile berbasis Android (Kotlin)

---

## 🚀 Tech Stack

### Backend (`be/`)
* **Bahasa & Framework**: Go 1.25, Fiber v2.52
* **Database & ORM**: Supabase (PostgreSQL), GORM v1.31 + Driver Postgres
* **AI Engine**: `google.golang.org/genai` (Google Gemini)
* **Library Tambahan**: golang-jwt v5, godotenv, google/uuid, swaggo/swag

### Frontend Android (`baper-andoid/`)
* **Bahasa**: Kotlin 2.2.10
* **UI Toolkit**: Jetpack Compose (BOM 2026.02.01)
* **Arsitektur & Navigasi**: MVVM, Navigation Compose
* **Networking**: Retrofit 2.9 + Gson, OkHttp 4.12
* **Penyimpanan Lokal**: DataStore Preferences
* **Lain-lain**: Lottie, Coil, AGP 9.3.1 (compileSdk 35, minSdk 24)

---

## 💻 Cara Menjalankan Secara Langsung (Native)

### 1. Menjalankan Backend (Go)
Pastikan Anda sudah berada di root direktori lalu masuk ke folder `be/`:
```bash
cd be

# Sesuaikan PATH environment Go pada mesin (jika diperlukan)
export PATH=$HOME/.local/go/bin:$PATH

# Menjalankan server HTTP utama (pastikan ada file .env untuk koneksi Supabase)
go run ./cmd/api
```
*(Catatan: Anda dapat menggenerate ulang dokumentasi Swagger dengan menjalankan `~/go/bin/swag init -g cmd/api/main.go` di folder be/)*

### 2. Menjalankan Aplikasi Android
Buka direktori proyek Android dan jalankan command Gradle:
```bash
cd baper-andoid

# Set Java JDK ke versi 21
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# Build aplikasi dalam mode debug (memerlukan flag --offline pada mesin ini)
./gradlew --offline assembleDebug
```
Setelah build berhasil, Anda dapat menginstal APK yang dihasilkan ke perangkat emulator atau _device_ Android asli.

---

## 🐳 Cara Menjalankan dengan Container (Docker / Podman)

Jika Anda ingin membungkus (*containerize*) backend agar dapat dijalankan secara terisolasi tanpa menginstal Go di sistem lokal Anda, gunakan Docker atau Podman. *(Asumsi file `Dockerfile` sederhana telah disiapkan di root folder `be/`)*.

### Menggunakan Docker
```bash
cd be

# Membangun docker image dari Dockerfile
docker build -t baper-backend:latest .

# Menjalankan container dengan me-mount environment variables dari .env
# (Misalkan aplikasi berjalan di port 3000)
docker run -d --name baper-api -p 3000:3000 --env-file .env baper-backend:latest
```

### Menggunakan Podman
Sintaks Podman pada dasarnya sama dengan Docker:
```bash
cd be

# Membangun image
podman build -t baper-backend:latest .

# Menjalankan container
podman run -d --name baper-api -p 3000:3000 --env-file .env baper-backend:latest
```

---

## 🌐 Ekspos Webhook ke Meta for Developers dengan Ngrok

Aplikasi ini menggunakan Webhook WhatsApp untuk menerima pesan pelanggan. Agar Meta dapat mengirimkan HTTP Request (event) dari server mereka ke server lokal Anda (yang berjalan di `localhost`), Anda harus membuat port lokal tersebut dapat diakses dari internet publik. Alat yang paling mudah digunakan untuk ini adalah **ngrok**.

### Langkah-langkah Setup Ngrok dan Verifikasi Meta:
1. **Jalankan Backend Lokal:** Pastikan backend Baper Anda sudah berjalan dengan sukses (misal: berjalan di port `3000`).
2. **Jalankan Ngrok:** Buka terminal baru dan jalankan ngrok pada port tempat backend berjalan:
   ```bash
   ngrok http 3000
   ```
3. **Salin URL Publik:** Pada layar ngrok, akan muncul URL Forwarding publik (misalnya: `https://a1b2-34-56.ngrok-free.app`). Salin URL HTTPS ini.
4. **Buka Meta for Developers:** Masuk ke dashboard aplikasi WhatsApp Anda di [Meta for Developers](https://developers.facebook.com/).
5. **Konfigurasi Webhook:** Di menu konfigurasi Webhook, masukkan data berikut:
   * **Callback URL**: Tempelkan URL ngrok Anda dan tambahkan path webhook sistem, menjadi:
     `https://a1b2-34-56.ngrok-free.app/api/webhook/`
   * **Verify Token**: Masukkan token rahasia webhook yang sama persis dengan yang ada di file `.env` Anda.
6. **Verifikasi dan Simpan:** Klik `Verify and Save`. Meta akan melakukan *handshake* ke endpoint `GET /api/webhook/` di sistem Anda. Jika konfigurasi benar, status webhook akan langsung menjadi terverifikasi.

> **⚠️ Peringatan Penting Ngrok:** Pada akun gratis, URL ngrok **akan selalu berubah** setiap kali Anda merestart proses ngrok. Jangan lupa untuk selalu memperbarui *Callback URL* di dashboard Meta setiap kali URL ngrok berganti agar bot tetap dapat merespon pesan.
