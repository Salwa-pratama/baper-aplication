# Walkthrough - Perbaikan Error MainActivity.kt

Saya telah memperbaiki berbagai error di `MainActivity.kt` yang disebabkan oleh dependensi yang hilang, kesalahan paket, dan penempatan resource yang salah.

## Perubahan yang Dilakukan

### Konfigurasi Build
- Menambahkan `com.airbnb.android:lottie-compose:6.7.1` ke [libs.versions.toml](file:///C:/Yasa/app/baper-aplication/baper-andoid/gradle/libs.versions.toml).
- Menambahkan dependensi tersebut ke [build.gradle.kts](file:///C:/Yasa/app/baper-aplication/baper-andoid/app/build.gradle.kts).

### Resource
- Membuat direktori `app/src/main/res/raw/`.
- Memindahkan `logo_vectorized.json` ke direktori `raw` tersebut sehingga bisa diakses melalui `R.raw.logo_vectorized`.

### Kode Sumber
- Memperbarui package name di [MainActivity.kt](file:///C:/Yasa/app/baper-aplication/baper-andoid/app/src/main/java/com/example/baper_andoid/MainActivity.kt) menjadi `com.example.baper_andoid`.
- Mengganti penggunaan tema dari `MyApplicationTheme` yang tidak ada menjadi `BaperandoidTheme`.
- Memperbaiki import Lottie dan tema.

## Hasil Verifikasi

### Build Gradle
- Berhasil menjalankan perintah `./gradlew :app:assembleDebug`.

### Status IDE
- Semua tanda merah (error) pada `MainActivity.kt` seharusnya sudah hilang.
