# Implementation Plan - Visual Swipe to Refresh (UI Only)

Menerapkan komponen `PullToRefreshBox` secara visual di seluruh tab utama aplikasi (Beranda, Produk, Rekap, Profil) tanpa melakukan hit API backend terlebih dahulu.

## User Review Required

> [!NOTE]
> - **Visual Only:** Untuk tahap ini, saat user men-swipe layar, indikator loading (circle) akan muncul selama 2 detik lalu menghilang. Tidak ada data yang benar-benar diperbarui dari backend.
> - **Integration:** Logika integrasi dengan API sesungguhnya akan dilakukan di tahap berikutnya setelah tampilan visual ini kamu setujui.

## Proposed Changes

### [UI Layer]

#### [MODIFY] [HomeScreen.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/home/HomeScreen.kt)
Membungkus seluruh konten tab utama dengan `PullToRefreshBox`.
- **Tab Beranda:** Membungkus `DashboardContent`.
- **Tab Produk & Rekap:** Membungkus `PlaceholderPage`.
- **Tab Profil:** Membungkus `ProfilScreen`.

Implementasi akan menggunakan state lokal `remember { mutableStateOf(false) }` untuk simulasi loading.

## Verification Plan

### Manual Verification
- Buka aplikasi dan masuk ke Dashboard.
- Swipe down di halaman Beranda, pastikan circle loading muncul.
- Pindah ke tab Produk, Rekap, dan Profil, lakukan hal yang sama.
- Pastikan circle loading menghilang secara otomatis setelah 2 detik.
