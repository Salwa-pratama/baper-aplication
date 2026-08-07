# Rencana Implementasi - Perbarui Tampilan LoginScreen.kt sesuai Login.json

Rencana ini bertujuan untuk mengubah tampilan `LoginScreen.kt` agar sesuai dengan desain yang dijelaskan dalam `Login.json` (desain modern dengan Card, input nomor WhatsApp, dan PIN), serta mengintegrasikan logika dari `LoginViewModel.kt`.

## Perubahan yang Diusulkan

### UI Screen

#### [MODIFY] [LoginScreen.kt](file:///C:/Yasa/app/baper-aplication/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/login/LoginScreen.kt)
- **Background**: Menggunakan warna latar belakang terang sesuai desain (`#F7F9F8`).
- **Header**: Menampilkan animasi Lottie `logo_vectorized` sebagai identitas brand.
- **Greeting**: Menambahkan teks "Selamat Datang!" dan sub-teks instruksi.
- **Login Card**: Menggunakan `Card` (atau `Surface` dengan elevasi) untuk membungkus form input:
    - **Nomor WhatsApp**: Input teks dengan awalan (prefix) "+62" dan ikon telepon.
    - **PIN Keamanan**: Input password dengan fitur toggle visibilitas (ikon mata).
- **Tombol Masuk**: Tombol "Masuk Sekarang" dengan warna hijau brand (`#107C42`) dan ikon panah ke kanan.
- **Footer**: Tautan navigasi "Belum punya akun? Daftar Sekarang" di bagian bawah.

### Logic & ViewModel

#### [MODIFY] [LoginViewModel.kt](file:///C:/Yasa/app/baper-aplication/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/login/LoginViewModel.kt)
- Tidak ada perubahan besar pada logika, hanya memastikan parameter `email` pada fungsi `login` diisi dengan nomor WhatsApp dari UI, dan `password` diisi dengan PIN. *Catatan: Jika backend memerlukan format khusus, pembersihan string (seperti menghapus spasi/karakter non-digit) akan dilakukan di sini atau di UI.*

## Rencana Verifikasi

### Tes Otomatis
- Menjalankan build `./gradlew :app:assembleDebug` untuk memastikan tidak ada kesalahan sintaks.

### Verifikasi Manual
- Memeriksa tampilan di Android Studio Preview atau Emulator:
    - Memastikan prefix "+62" muncul benar.
    - Memastikan toggle mata pada PIN berfungsi.
    - Memastikan animasi Lottie muncul di bagian atas.
    - Memastikan navigasi ke Register tetap berfungsi melalui tombol di footer.
