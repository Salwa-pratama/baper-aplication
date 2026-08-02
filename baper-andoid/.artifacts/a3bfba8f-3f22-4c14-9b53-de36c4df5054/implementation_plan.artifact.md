# Rencana Sinkronisasi Login dengan Backend (Versi Terbaru)

Berdasarkan struct Go dan JSON terbaru yang kamu kasih, struktur responsnya sekarang lebih simpel (satu level `data`) dan `status` sudah kembali menjadi `Boolean`. Saya akan menyesuaikan kodingan Android agar sinkron dengan perubahan ini.

## Perubahan yang Diusulkan

### Data Transfer Object (DTO)

#### [MODIFY] [LoginResponse.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/response/LoginResponse.kt)
- Mengubah `status` dari `String` menjadi `Boolean` sesuai dengan `AuthResponse` di backend.
- Memastikan `data` langsung merujuk ke `AuthData?`.

### Business Logic

#### [MODIFY] [LoginViewModel.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/login/LoginViewModel.kt)
- Membersihkan variabel sisa (`nestedData`) yang sudah tidak digunakan.
- Memperbaiki logika pengecekan `if (response.status)` menggunakan tipe `Boolean`.
- Menyederhanakan logging agar sesuai dengan struktur data yang baru.

## Langkah Verifikasi
1.  Build project untuk memastikan tidak ada error kompilasi pada tipe data `Boolean`.
2.  Test login dan cek Logcat untuk memastikan token berhasil diekstrak dari `response.data.access_token`.
