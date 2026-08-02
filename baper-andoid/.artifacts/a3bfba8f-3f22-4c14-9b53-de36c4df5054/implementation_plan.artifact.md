# Rencana Redesign Home Screen & Fitur Refresh

Gue setuju bro, tampilan "Cukur" kemarin emang gak nyambung sama konsep "Baper" (Bantu Pesan & Rekap). Kita bakal ubah total jadi Dashboard Bisnis yang profesional dan nambahin fitur **Pull-to-Refresh**.

## User Review Required
> [!IMPORTANT]
> - Warna tema akan diubah dari Hijau WhatsApp ke **Brand Green (0xFF28A745)** agar sinkron dengan Splash & Onboarding.
> - Data dummy "Toko Barbershop" akan diganti menjadi **"Statistik Pesanan"** dan **"Riwayat Rekap"**.

## Proposed Changes

### Logic & Data

#### [MODIFY] [HomeViewModel.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/home/HomeViewModel.kt)
- Menambahkan state untuk `isRefreshing` dan data bisnis (Total Omzet, Jumlah Pesanan).
- Implementasi fungsi `refreshData()` dengan simulasi delay.

#### [NEW] [HomeViewModelFactory.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/home/HomeViewModelFactory.kt)
- Membuat factory agar `HomeViewModel` bisa di-inject ke UI.

### UI Screens

#### [MODIFY] [HomeScreen.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/home/HomeScreen.kt)
- Mengganti layout "Cukur" menjadi Dashboard.
- Implementasi `PullToRefreshBox` (Material 3) untuk fitur refresh halaman.
- Membuat widget **Stat Card** untuk ringkasan Pesanan & Rekap.
- Mengubah list menjadi **"Transaksi Terbaru"**.

## Verification Plan

### Manual Verification
- Buka Home Screen, pastikan warna sudah hijau profesional.
- Tarik layar ke bawah (swipe down) untuk memastikan fitur **Refresh** muncul dan data terupdate.
- Pastikan teks sapaan dan data sudah relevan dengan "Pesan & Rekap".
