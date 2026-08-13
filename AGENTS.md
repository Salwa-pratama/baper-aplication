# AGENTS.md — Baper (Bantu Pesan dan Rekap)

Instruksi wajib untuk AI agent yang bekerja di repo ini.
Baca sampai habis SEBELUM menulis satu baris kode.

---

## 0. ATURAN NOMOR SATU: PAHAMI STRUKTUR DULU

**Jangan pernah generate kode sebelum menyelesaikan checklist ini.**

1. Baca peta folder di bagian 2 (backend) dan bagian 3 (Android) di file ini.
2. Buka file yang SEJENIS dengan yang mau kamu buat, dan tiru polanya.
   Contoh: mau bikin endpoint baru? Baca dulu SELURUH
   `be/internal/modules/features/products/` (5 file) sebagai template.
   Mau bikin screen baru? Baca dulu `baper-andoid/.../ui/screen/produk/` (3 file).
3. Cek apakah model/DTO/repository yang kamu butuhkan SUDAH ADA.
   Jangan bikin duplikat. `be/internal/models/` sudah punya 10 entity.
4. Cek dependency yang sudah terpasang: `be/go.mod` dan
   `baper-andoid/gradle/libs.versions.toml` + `app/build.gradle.kts`.
   **Jangan tambah library baru tanpa izin eksplisit.**
5. Lapor ke user: file apa yang akan dibuat/diubah, di path mana, dan kenapa.
   **Tunggu konfirmasi. Baru koding.**

Kalau kamu tidak yakin sebuah file harus ditaruh di mana — TANYA.
Jangan mengarang folder baru.

---

## 1. GAMBARAN PROYEK

Baper = sistem bantu pesan & rekap untuk UMKM. Bot WhatsApp menerima pesan
pelanggan, dijawab oleh AI (Gemini), pesanan dicatat otomatis, lalu pemilik
usaha memantau dan merekapnya dari aplikasi Android.

Dua repo dalam satu git root:

```
baper/                      ← git root (JANGAN cari .git di dalam be/)
├── AGENTS.md               ← file ini
├── be/                     ← backend  : Go 1.25, Fiber v2, GORM, Supabase Postgres
└── baper-andoid/           ← frontend : Kotlin, Jetpack Compose, MVVM, Retrofit
```

Stack backend: Fiber v2.52, GORM 1.31 + driver postgres, golang-jwt v5,
google.golang.org/genai (Gemini), godotenv, google/uuid, swaggo/swag.
Stack Android: Compose BOM 2026.02.01, Kotlin 2.2.10, AGP 9.3.1, compileSdk 35,
minSdk 24, Retrofit 2.9 + Gson, OkHttp 4.12, DataStore Preferences,
navigation-compose, Lottie, Coil.

Konteks penting: ini **proyek lomba**, bukan produksi. Prioritas = fitur jalan
dan struktur rapi. Jangan menambahkan kontrol keamanan yang butuh kredensial
pihak ketiga atau infrastruktur tambahan tanpa diminta.

---

## 2. BACKEND (`be/`) — DI MANA KODE HARUS DITARUH

### 2.1 Peta folder

```
be/
├── cmd/                          ← HANYA entry point (func main). Tidak ada logika bisnis.
│   ├── api/main.go               ← server HTTP utama
│   ├── migration/main.go         ← runner migrasi
│   └── seeding/main.go           ← seeder data awal
├── config/database.go            ← DUPLIKAT dari internal/config (dipakai cmd/migration).
│                                    JANGAN tambah apa pun di sini.
├── internal/
│   ├── common/
│   │   ├── apperror/             ← tipe error aplikasi + constructor
│   │   └── res/                  ← bentuk response JSON + HandleError
│   ├── config/                   ← DbSupabase(), SetupSwagger()
│   ├── database/migrations/       ← file migrasi (migration_001.go, dst)
│   ├── middleware/               ← AuthMiddleware dan middleware lain
│   ├── models/                   ← SEMUA struct GORM (tabel database)
│   ├── modules/
│   │   ├── auth/                 ← modul autentikasi (di luar features/)
│   │   └── features/             ← SEMUA modul fitur bisnis
│   │       ├── bot/
│   │       ├── busines/          ← typo lama, jangan diperbaiki tanpa izin
│   │       ├── chat/             ← webhook WhatsApp + Gemini
│   │       ├── conversation/
│   │       ├── orders/
│   │       └── products/
│   ├── router/router.go          ← agregator: daftarkan modul baru di sini
│   └── utils/                    ← helper lintas modul (JWT, UUID)
└── docs/                         ← HASIL GENERATE swag. Jangan edit manual.
```

### 2.2 Tabel keputusan — kode baru masuk ke mana

| Kamu mau menulis... | Taruh di |
|---|---|
| Tabel/entity database baru | `internal/models/<nama_tunggal>.go` |
| Endpoint fitur baru (satu domain utuh) | folder BARU `internal/modules/features/<nama>/` berisi 5 file (lihat 2.3) |
| Endpoint tambahan di fitur yang sudah ada | edit `routes.go` + `controller.go` + `service.go` + `repository.go` modul itu |
| Struct request/response HTTP | `dto.go` di dalam modul yang bersangkutan. **BUKAN** di `internal/models/` |
| Query database | `repository.go` modul itu. Tidak ada `db.` di controller atau service |
| Logika bisnis / validasi kepemilikan | `service.go` modul itu |
| Parsing request & pembentukan response | `controller.go` modul itu |
| Middleware baru | `internal/middleware/<nama>_middleware.go` |
| Helper yang dipakai >1 modul | `internal/utils/<nama>.go` |
| Jenis error HTTP baru | `internal/common/apperror/app_error.go` |
| Migrasi skema | `internal/database/migrations/migration_00N.go` |

Aturan keras:
- Tidak ada file `.go` baru langsung di `be/` root atau `internal/` root.
- Modul fitur TIDAK BOLEH mengimpor modul fitur lain. Kalau butuh data lintas
  domain, tambahkan method di `repository.go` modul kamu sendiri.
- `internal/models/` tidak boleh mengimpor apa pun dari `internal/modules/`.
- Kalau membuat modul baru, WAJIB tambahkan `<nama>.InitRoutes(api, db)` di
  `internal/router/router.go`.

### 2.3 Anatomi satu modul fitur (WAJIB 5 file, nama persis begini)

Template acuan: `internal/modules/features/products/`. Struktur:

```
<fitur>/
├── routes.go       ← InitRoutes + wiring dependency + pemasangan middleware
├── dto.go          ← CreateXRequest, UpdateXRequest, XResponse
├── controller.go   ← struct Controller, method per endpoint
├── service.go      ← interface Service + struct service
└── repository.go   ← interface Repository + struct repository
```

Alur data selalu satu arah:

```
router.SetupRoutes → <fitur>.InitRoutes → Group + AuthMiddleware
    → Controller (parse & respond)
        → Service (logika bisnis)
            → Repository (GORM)
                → models.X
```

**routes.go** — di sini juga tempat dependency injection-nya:

```go
package <fitur>

import (
	"baper/internal/middleware"

	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func InitRoutes(router fiber.Router, db *gorm.DB) {
	repo := NewXRepository(db)
	svc := NewXService(repo)
	ctrl := NewXController(svc)

	g := router.Group("/<path>", middleware.AuthMiddleware())

	g.Post("/", ctrl.CreateX)
	g.Get("/", ctrl.GetAllX)
	g.Get("/:id", ctrl.GetXByID)
	g.Put("/:id", ctrl.UpdateX)
	g.Delete("/:id", ctrl.DeleteX)
}
```

**repository.go** — interface dulu, lalu implementasi. Selalu `WithContext(ctx)`:

```go
type Repository interface {
	FindBusinessByUserID(ctx context.Context, userID string) (*models.Business, error)
	CreateX(ctx context.Context, x *models.X) error
}

type repository struct {
	db *gorm.DB
}

func NewXRepository(db *gorm.DB) Repository {
	return &repository{db}
}

func (r *repository) CreateX(ctx context.Context, x *models.X) error {
	return r.db.WithContext(ctx).Create(x).Error
}
```

**service.go** — parameter kedua SELALU `userID` hasil JWT:

```go
type Service interface {
	CreateX(ctx context.Context, userID string, req CreateXRequest) (*XResponse, error)
}

type service struct {
	repo Repository
}

func NewXService(repo Repository) Service {
	return &service{repo}
}
```

**controller.go** — ambil user_id dari Locals, jangan pernah dari body:

```go
func currentUserID(ctx *fiber.Ctx) (string, error) {
	userID, ok := ctx.Locals("user_id").(string)
	if !ok || userID == "" {
		return "", apperror.Unauthorized("unauthorized")
	}
	return userID, nil
}

func (c *Controller) CreateX(ctx *fiber.Ctx) error {
	userID, err := currentUserID(ctx)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	var req CreateXRequest
	if err := ctx.BodyParser(&req); err != nil {
		return res.HandleError(ctx, apperror.BadRequest("Format request tidak valid"))
	}

	data, err := c.service.CreateX(ctx.Context(), userID, req)
	if err != nil {
		return res.HandleError(ctx, err)
	}

	return ctx.Status(fiber.StatusCreated).JSON(res.Success("X berhasil dibuat", data))
}
```

### 2.4 Konvensi backend yang tidak boleh dilanggar

**Bentuk response — SATU bentuk untuk semua endpoint:**

```json
{ "status": true, "message": "pesan bahasa Indonesia", "data": {} }
```

Selalu lewat `res.Success(msg, data)` atau `res.Error(msg)`.
Untuk error, selalu `return res.HandleError(ctx, err)` — jangan bikin
`fiber.Map{}` sendiri, jangan bikin struct Response baru per modul.
(Modul `chat/` punya `Response` sendiri dengan `Status string`; itu warisan
lama, JANGAN dijadikan contoh dan jangan diperluas.)

**Error selalu lewat apperror:**
`apperror.BadRequest / Unauthorized / Forbidden / NotFound / Conflict / Internal`.
Jangan `errors.New()` polos di service. Jangan bocorkan pesan error asli
database ke client — `res.HandleError` sudah menahan itu jadi 500 generik.

**Kepemilikan data — pola paling penting di proyek ini:**
- `business_id` DILARANG ada di DTO request. Ambil dari JWT:
  `service.businessIDOf(ctx, userID)` → `repo.FindBusinessByUserID`.
- Resource milik bisnis lain dijawab **404, bukan 403** (jangan bocorkan
  keberadaan data orang lain).
- Endpoint baru default-nya WAJIB pakai `middleware.AuthMiddleware()`.
  Satu-satunya pengecualian yang sudah ada: `/api/auth/*` dan
  `GET /api/webhook/` + `POST /api/webhook/verify` (Meta tidak punya JWT kita).

**Model GORM:**
- ID = `string` `varchar(36)`, UUID v4, digenerate di hook `BeforeCreate`.
  Bukan auto-increment.
- Field rahasia wajib `json:"-"` (contoh: `User.PasswordHash`, `AuthToken.Token`).
- Tag lengkap: `gorm:"type:...;not null" json:"snake_case"`.

**Tag `validate:"required"`:** ada di DTO tapi TIDAK ADA library validator di
go.mod — tag itu dekoratif. Validasi nyata harus manual di controller/service.
Jangan berasumsi tag itu jalan.

**Swagger:** setiap handler baru wajib punya blok komentar `// X godoc` seperti
di `products/controller.go`. Setelah itu regenerate:
```bash
export PATH=$HOME/.local/go/bin:$PATH
~/go/bin/swag init -g cmd/api/main.go
```

**Penamaan:** package huruf kecil satu kata. Constructor
`NewXRepository/NewXService/NewXController`. File snake_case.
Komentar dan pesan user berbahasa Indonesia.

### 2.5 Endpoint yang sudah ada (jangan bikin duplikat)

```
GET    /                                     health check
GET    /swagger/*
POST   /api/auth/login                       publik
POST   /api/auth/register                    publik
POST   /api/auth/refresh                     publik (refresh token di body)
GET    /api/webhook/                         publik (handshake Meta)
POST   /api/webhook/verify                   publik (pesan masuk Meta)
POST   /api/webhook/send-message             JWT
POST   /api/webhook/send-media               JWT
POST   /api/webhook/upload-media             JWT
POST   /api/business/register                JWT
GET    /api/business/recap/monthly           JWT
GET    /api/business/recap/monthly/export    JWT
GET    /api/products                         JWT
POST   /api/products                         JWT
GET    /api/products/:id                     JWT
PUT    /api/products/:id                     JWT
DELETE /api/products/:id                     JWT
GET    /api/bots/mine                        JWT
PATCH  /api/bots/:id/toggle                  JWT
PUT    /api/bots/:id/prompt                  JWT
GET    /api/conversations                    JWT
GET    /api/conversations/:id/messages       JWT
GET    /api/orders                           JWT
PATCH  /api/orders/:id/status                JWT
```

### 2.6 Model & relasi yang sudah ada

```
User 1─1 Business 1─1 Bot
              ├─* Product ─* OrderItem
              ├─* Customer
              └─* Order ─* OrderItem
Bot 1─* ChatSession *─1 Customer
ChatSession 1─* Message
ChatSession 1─* Order          (Order.SessionID nullable)
User 1─* AuthToken
```

File: `auth_token.go bot.go business.go chat_session.go customer.go
message.go order.go order_item.go product.go user.go`.
`Order.Status` adalah enum `OrderStatus` = `"paid"` | `"unpaid"` dengan
method `IsValid()`.

---

## 3. ANDROID (`baper-andoid/`) — DI MANA KODE HARUS DITARUH

### 3.1 Peta paket

Root paket: `com.example.baper_andoid` di
`baper-andoid/app/src/main/java/com/example/baper_andoid/`

```
com.example.baper_andoid/
├── MainActivity.kt            ← satu-satunya Activity. Jangan tambah Activity baru.
├── data/
│   ├── local/                 ← DataStore / penyimpanan lokal
│   ├── remote/
│   │   ├── ApiService.kt      ← SATU interface Retrofit untuk semua endpoint
│   │   ├── RetrofitClient.kt  ← object singleton + interceptor
│   │   └── dto/
│   │       ├── request/       ← XRequest.kt
│   │       └── response/      ← XResponse.kt (+ XItem, XListResponse, XDetailResponse)
│   └── repository/            ← XRepository.kt (satu per domain)
├── domain/model/              ← saat ini praktis kosong. Jangan pakai kecuali diminta.
├── navigation/
│   ├── Screen.kt              ← sealed class route
│   ├── BottomNavItem.kt
│   └── NavGraph.kt            ← NavHost + instansiasi ViewModel level atas
└── ui/
    ├── components/            ← Composable yang dipakai >1 screen
    ├── theme/                 ← Color.kt, Theme.kt, Type.kt
    └── screen/<fitur>/        ← XScreen.kt + XViewModel.kt + XViewModelFactory.kt
```

Folder lain: `app/src/main/res/raw/` untuk JSON Lottie,
`res/drawable/` untuk vector, `res/xml/network_security_config.xml`.

### 3.2 Tabel keputusan — kode baru masuk ke mana

| Kamu mau menulis... | Taruh di |
|---|---|
| Layar baru | folder BARU `ui/screen/<fitur>/` berisi 3 file (lihat 3.3) |
| Composable yang dipakai >1 layar | `ui/components/<Nama>.kt` |
| Composable yang dipakai 1 layar saja | private function di file `XScreen.kt` itu sendiri |
| Definisi endpoint HTTP | tambah method di `data/remote/ApiService.kt` (jangan bikin interface baru) |
| Body request | `data/remote/dto/request/XRequest.kt` |
| Bentuk response | `data/remote/dto/response/XResponse.kt` |
| Pemanggilan API + mapping | `data/repository/XRepository.kt` |
| State & logika layar | `ui/screen/<fitur>/XViewModel.kt` |
| Penyimpanan lokal | `data/local/` |
| Rute navigasi baru | tambah `object` di `navigation/Screen.kt`, lalu `composable{}` di `NavGraph.kt` |
| Warna / font / bentuk | `ui/theme/` — jangan hardcode `Color(0xFF...)` di dalam Screen |

Aturan keras:
- Composable TIDAK BOLEH memanggil `ApiService` atau `Repository` langsung.
  Selalu lewat ViewModel.
- ViewModel TIDAK BOLEH mengimpor apa pun dari `androidx.compose.ui` atau
  menyimpan `Context`.
- Repository TIDAK BOLEH tahu apa pun soal Compose atau ViewModel.
- Jangan tambah Gradle module baru. Proyek ini single-module (`:app`).
- Dependency baru didaftarkan di `gradle/libs.versions.toml` lalu dirujuk
  dengan `libs.x.y` di `app/build.gradle.kts` — jangan tulis koordinat
  Maven langsung kalau bisa lewat version catalog.

### 3.3 Anatomi satu layar (3 file)

Template acuan: `ui/screen/produk/`.

```
ui/screen/<fitur>/
├── <Fitur>Screen.kt           ← @Composable, terima ViewModel + callback navigasi
├── <Fitur>ViewModel.kt        ← data class XUiState + class XViewModel
└── <Fitur>ViewModelFactory.kt ← ViewModelProvider.Factory (DI manual)
```

**UiState — satu data class, semua state dalam satu objek:**

```kotlin
data class XUiState(
    val isLoading: Boolean = false,
    val items: List<XItem> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false
)
```

**ViewModel — MutableStateFlow privat, StateFlow publik:**

```kotlin
class XViewModel(private val repository: XRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(XUiState())
    val uiState: StateFlow<XUiState> = _uiState.asStateFlow()

    init { loadItems() }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = repository.getItems()
                if (response.status) {
                    _uiState.value = _uiState.value.copy(items = response.data, error = null)
                } else {
                    _uiState.value = _uiState.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.localizedMessage ?: "Gagal mengambil data"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
```

Wajib: cek `response.status` DAN `try/catch` DAN `finally` untuk mematikan
loading. Pesan error fallback berbahasa Indonesia.

**Factory — pola persis begini:**

```kotlin
class XViewModelFactory(private val repository: XRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(XViewModel::class.java)) {
            return XViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

**Wiring di NavGraph.kt** — ViewModel dibuat di level atas NavGraph supaya
bisa dibagi antar layar:

```kotlin
val xRepository = remember { XRepository(RetrofitClient.getInstance(context)) }
val xViewModel: XViewModel = viewModel(factory = XViewModelFactory(xRepository))
```

Tidak ada Hilt, tidak ada Koin. DI-nya manual. Jangan perkenalkan DI framework.

### 3.4 Konvensi DTO Android

Backend selalu mengirim `{status, message, data}`. Karena `data` berubah bentuk
per endpoint, satu domain bisa punya beberapa DTO response dalam SATU file:

```kotlin
// data/remote/dto/response/ProductResponse.kt
data class ProductItem(...)                                    // entity
data class ProductResponse(status, message, data: ProductItem?)      // create/update/delete
data class ProductListResponse(status, message, data: List<ProductItem>) // GET all
data class ProductDetailResponse(status, message, data: ProductItem)    // GET by id
```

Ikuti pola ini. Setiap field WAJIB pakai `@SerializedName("snake_case")` yang
cocok dengan JSON backend — properti Kotlin camelCase, JSON snake_case.
Jangan mengandalkan nama properti saja (Gson, bukan kotlinx.serialization).

Kalau `data` bisa apa saja, pakai `Any?` (lihat `ChatResponse.kt`) — tapi
lebih baik bikin tipe konkret kalau bentuknya diketahui.

### 3.5 Konvensi jaringan

`RetrofitClient` punya dua jalur:

- `RetrofitClient.instance` — tanpa auth. HANYA untuk login, register, refresh.
- `RetrofitClient.getInstance(context)` — dengan 3 interceptor: logging,
  `Authorization: Bearer <token>`, dan auto-refresh saat 401.

Semua endpoint yang butuh login WAJIB dipanggil lewat `getInstance(context)`.

`BASE_URL` sudah berakhir dengan `/api/`, jadi path di `ApiService` **TIDAK
BOLEH** diawali `api/` lagi — kalau tidak jadi `/api/api/products` → 404.
Tulis `@GET("products")`, bukan `@GET("api/products")`.

Semua method `ApiService` adalah `suspend`.

`BASE_URL` masih hardcode IP LAN di `RetrofitClient.kt`. Kalau IP berubah,
itu satu-satunya tempat yang perlu diedit.

### 3.6 Penamaan Android

- File Composable & class: PascalCase (`HomeScreen.kt`).
  Catatan: `produk.kt` dan `rekap.kt` masih lowercase — itu warisan lama,
  file BARU harus PascalCase.
- Route di `Screen.kt`: snake_case (`"lihat_pesanan"`), dengan helper
  `createRoute()` kalau ada argumen.
- Teks UI dan pesan error: bahasa Indonesia.

---

## 4. PERINTAH BUILD & TEST

Environment mesin ini punya beberapa keanehan. Pakai perintah persis ini.

### Backend

```bash
cd be
export PATH=$HOME/.local/go/bin:$PATH      # go TIDAK ada di PATH sistem
go build ./...
go vet ./...
go test ./...
go run ./cmd/api                            # server di APP_PORT dari be/.env
~/go/bin/swag init -g cmd/api/main.go       # regenerate swagger
```

PERINGATAN: `config.DbSupabase()` memanggil `godotenv.Overload()` yang
**menimpa** environment variable dari `be/.env`. Kalau kamu mau menjalankan
binary dengan DSN/port lain, jalankan dari direktori yang tidak punya `.env` —
kalau tidak dia akan tetap menyambung ke Supabase yang sebenarnya.

Jangan commit binary hasil build. `be/api` sudah di-gitignore,
`be/baper-api` belum — jangan `git add` file itu.

### Android

```bash
cd baper-andoid
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew --offline assembleDebug
./gradlew --offline test
```

Android SDK ada di `/media/pratama/Data1/android/android-sdk`
(sudah tercatat di `local.properties`). Flag `--offline` diperlukan di mesin ini.

### Git

Git root ada di `baper/`, BUKAN di `be/` atau `baper-andoid/`.
Semua perintah git dijalankan dari `baper/`.
Jangan commit tanpa diminta user secara eksplisit.

---

## 5. UTANG TEKNIS YANG DIKETAHUI — jangan diperbaiki tanpa izin

Ini sudah diinventarisasi. Jangan "membetulkan" sambil mengerjakan tugas lain,
dan jangan menjadikannya contoh pola yang benar.

1. `be/config/database.go` duplikat identik dengan `be/internal/config/database.go`.
   Yang di luar hanya dipakai `cmd/migration`.
2. `domain/model/User.kt` di Android kosong — layer domain praktis tidak ada.
   Repository mengembalikan DTO langsung ke ViewModel. Itu memang polanya sekarang.
3. `ProfilViewModel` berisi data dummy hardcode (nama, email, statistik).
   Belum terhubung API.
4. `HomeRepository.getDashboardStats()` dan `getRecentTransactions()` masih
   mengembalikan data simulasi, bukan hasil API.
5. Package `busines` kurang satu huruf 's' padahal route-nya `/business`.
6. Tag `validate:"required"` tidak dieksekusi (tidak ada validator di go.mod).
7. Modul `chat/` punya struct `Response` sendiri (`Status string`) dan
   mencampur `fiber.Map`, `resp`, dan `res.Success`. Tidak konsisten dengan
   modul lain — modul BARU harus pakai `res.Response`.
8. Layar `rekap/` tidak punya ViewModel; state-nya di dalam Composable.
9. File Compose besar: `HomeScreen.kt` (~679 baris), `produk.kt` (~642),
   `RegisterScreen.kt` (~563), `OnBoardingScreen.kt` (~500).
10. `AllowOrigins: "*"` di CORS `cmd/api/main.go`.

---

## 6. CARA BEKERJA DENGAN USER INI

- **Balas dalam bahasa Indonesia**, gaya santai.
- **Tanya dulu sebelum koding** untuk perubahan yang menyentuh lebih dari satu
  file. Sajikan rencana, tunggu persetujuan.
- **Kerjakan bertahap.** Selesaikan satu tahap, lapor hasilnya, baru lanjut.
  Jangan kirim satu batch perubahan raksasa.
- **Perbaikan harus bedah presisi.** Ubah hanya yang berkaitan dengan masalah
  yang diminta. JANGAN menulis ulang file secara total, jangan merapikan kode
  di sekitarnya, jangan menambah abstraksi yang tidak diminta.
- **Jangan tambah dependency, framework, atau folder baru** tanpa izin.
- **Jangan bikin file dokumentasi/markdown baru** kecuali diminta.
- Verifikasi sebelum melapor selesai: `go build ./...` untuk backend,
  `./gradlew --offline assembleDebug` untuk Android. Kalau tidak bisa
  diverifikasi, katakan apa adanya — jangan mengarang hasil.
