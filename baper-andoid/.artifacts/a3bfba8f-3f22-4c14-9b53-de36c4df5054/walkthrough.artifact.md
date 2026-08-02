# Walkthrough - Perbaikan Double Nested JSON Parsing

Gue udah memperbaiki masalah parsing JSON yang bikin login kamu selalu merah meskipun backend sukses. Masalahnya ada pada struktur JSON backend kamu yang ternyata punya hirarki bertumpuk (nested).

## Perubahan yang Dilakukan

### 1. Sinkronisasi Struktur DTO (`LoginResponse.kt`)
Gue menyesuaikan struktur class agar sesuai dengan JSON asli dari backend kamu:
- **String Status**: `status` di level teratas sekarang dibaca sebagai `String` ("success") sesuai kiriman backend.
- **Double Nesting**: Menambahkan class `LoginNestedData` karena token kamu dibungkus dua kali dalam field `data`.
  - Hirarki sekarang: `Response` -> `data` (NestedData) -> `data` (AuthData) -> `access_token`.

### 2. Logika Parsing Baru (`LoginViewModel.kt`)
- Gue update pengecekan suksesnya jadi: `response.status == "success"` DAN `response.data.status == true`.
- Pengambilan token sekarang diarahkan ke jalan yang benar: `response.data.data.access_token`.
- Gue tambahin log yang lebih detail buat tiap level nesting biar kita gampang mantau.

## Kenapa Sebelumnya "Gagal"?
Berdasarkan log yang kamu kasih:
- **Android**: Ngarep `status` itu `Boolean` (true/false), tapi **Backend** ngirim `"success"` (String). Android bingung dan anggap itu `false`.
- **Android**: Nyari token langsung di dalam `data`, tapi **Backend** nyimpen di dalam `data.data`. Android gak nemu dan anggap tokennya `null`.

## Hasil Akhir
Sekarang aplikasi kamu udah bisa baca hirarki JSON yang kompleks itu dengan benar.

> [!TIP]
> Sekarang coba **Run** lagi aplikasinya. Harusnya langsung masuk ke Home karena rute pengambilan tokennya udah gue benerin sesuai peta JSON backend kamu!
