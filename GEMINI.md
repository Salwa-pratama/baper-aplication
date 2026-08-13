# GEMINI.md

Baca dan patuhi `AGENTS.md` di folder yang sama (root repo `baper/`).
Isinya lengkap: peta folder backend Go dan Android Kotlin, tabel keputusan
"kode baru ditaruh di mana", template tiap layer, perintah build, dan
daftar utang teknis yang tidak boleh diutak-atik tanpa izin.

Ringkasan aturan paling penting:

1. PAHAMI STRUKTUR DULU. Jangan generate kode sebelum membaca peta folder di
   AGENTS.md dan membuka file sejenis sebagai template.
2. Backend: setiap fitur = folder di `be/internal/modules/features/<nama>/`
   dengan 5 file wajib — `routes.go dto.go controller.go service.go repository.go`.
   Alur satu arah: Controller → Service → Repository → models.
3. Android: setiap layar = folder di
   `baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/<fitur>/`
   dengan 3 file — `XScreen.kt XViewModel.kt XViewModelFactory.kt`.
   Composable tidak boleh menyentuh Repository/ApiService langsung.
4. DTO backend di `dto.go` modulnya, BUKAN di `internal/models/`.
   DTO Android di `data/remote/dto/request/` dan `data/remote/dto/response/`.
5. Semua response backend berbentuk `{status, message, data}` lewat
   `res.Success()` / `res.HandleError()`.
6. `business_id` tidak pernah datang dari body request — selalu dari JWT.
7. Jangan tambah library, folder, atau Gradle module baru tanpa izin user.
8. Lapor rencana ke user dalam bahasa Indonesia, tunggu konfirmasi, kerjakan
   bertahap, dan lakukan perbaikan bedah presisi — bukan rewrite total.
