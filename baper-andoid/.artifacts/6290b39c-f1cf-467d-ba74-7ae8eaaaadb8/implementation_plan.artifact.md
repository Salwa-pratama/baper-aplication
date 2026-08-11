# Implementation Plan - Auth Integration & Home Loading

Menyambungkan fitur Login/Register dengan session management (DataStore) dan menambahkan loading indicator di modul Home.

## User Review Required

> [!IMPORTANT]
> - **Session Storage:** Saya akan menggunakan Jetpack DataStore di `UserPreferences.kt` untuk menyimpan token login secara aman.
> - **Auto-Login:** Alur aplikasi akan diubah: Splash Screen -> Cek Token -> (Home jika ada, Login jika tidak ada).
> - **Retrofit Auth:** Seluruh request ke API akan otomatis menyertakan header `Authorization: Bearer <token>` jika token tersedia.

## Proposed Changes

### [Data Layer]

#### [MODIFY] [UserPreferences.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/local/UserPreferences.kt)
Implementasi `DataStore` untuk menyimpan dan mengambil `authToken`.

#### [MODIFY] [RetrofitClient.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/RetrofitClient.kt)
Menambahkan `AuthInterceptor` untuk menyisipkan token ke header API secara otomatis.

### [UI Layer - Login & Register]

#### [MODIFY] [LoginViewModel.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/login/LoginViewModel.kt)
Simpan token ke `UserPreferences` setelah login berhasil.

#### [NEW] [RegisterViewModel.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/register/RegisterViewModel.kt)
Implementasi logika register dan error handling.

### [UI Layer - Home & Navigation]

#### [MODIFY] [HomeScreen.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/home/HomeScreen.kt)
Observasi `isLoading` dari `HomeViewModel` dan tampilkan `CircularProgressIndicator` di tengah layar saat data sedang dimuat.

#### [MODIFY] [NavGraph.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/navigation/NavGraph.kt)
- Ubah `startDestination` ke `Screen.Splash.route`.
- Tambahkan logika pengecekan session di Splash Screen untuk menentukan tujuan navigasi berikutnya.

## Verification Plan

### Manual Verification
1. Jalankan aplikasi, harus masuk ke Splash Screen.
2. Jika belum login, harus diarahkan ke halaman Login.
3. Coba Login dengan akun valid, pastikan diarahkan ke Home.
4. Di Home, pastikan muncul loading indicator sebentar sebelum data tampil.
5. Tutup dan buka kembali aplikasi, pastikan langsung masuk ke Home (Auto-login).
