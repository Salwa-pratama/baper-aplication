# Implementasi Peningkatan Tata Letak & Simetri Onboarding

Rencana ini bertujuan untuk merapikan tata letak, padding, dan spasi di `OnBoardingScreen.kt` agar terlihat lebih profesional, simetris, dan estetis (minimalis).

## Proposed Changes

### [Component Name] Onboarding UI

#### [MODIFY] [OnBoardingScreen.kt](file:///C:/Yasa/app/baper-aplication/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/onboarding/OnBoardingScreen.kt)

**1. Standardisasi Spasi & Padding (DashboardPreview):**
- Menyamakan padding luar `Column` menjadi `16.dp` agar seimbang.
- Menyamakan padding dalam `Card` menjadi `16.dp` untuk memberikan "ruang napas" yang lebih baik.
- Menyesuaikan `Arrangement.spacedBy` menjadi `12.dp` (kelipatan 4/8 yang lebih standar).
- Merapikan `RekapItem` agar ikon profil dan teks sejajar dengan sempurna.

**2. Peningkatan Simetri & Aliran Visual (BaperOnboardingScreen):**
- Menstandardisasi padding horizontal utama menjadi `24.dp` atau `32.dp` di seluruh layar.
- Mengatur ulang `Spacer` antara elemen (Logo/Dashboard -> Judul -> Deskripsi) agar memiliki rasio yang lebih estetis.
- Memastikan teks judul dan deskripsi memiliki padding horizontal yang simetris dan konsisten di semua slide.
- Merapikan bagian bawah (Tombol & Indikator) agar memiliki jarak yang lebih seimbang terhadap tepi layar.

**3. Detail Halus (Fine-Tuning):**
- Menyesuaikan ukuran logo dan mockup sedikit lagi agar komposisi visual di tengah layar terasa lebih "mantap".
- Menyeimbangkan posisi "Blob" cahaya di background agar tidak terlalu mendominasi satu sisi.

## Verification Plan

### Manual Verification
1. **Cek Seluruh Slide**: Memastikan setiap slide (1, 2, dan 3) terlihat rapi, teks tidak terlalu mepet ke pinggir, dan elemen berada di tengah secara visual.
2. **Cek Konsistensi**: Memastikan spasi antara judul dan deskripsi sama di setiap slide.
3. **Cek Tombol**: Memastikan tombol "Buat Akun" dan "Masuk" terlihat kokoh dan simetris terhadap lebar layar.

Apakah Anda setuju dengan rencana perapihan tata letak ini?
