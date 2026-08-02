# Rencana Perbaikan Parsing JSON Login (Double Nested)

Berdasarkan log yang kamu kasih, JSON dari backend kamu itu punya struktur "Double Nested" (data di dalam data) dan tipe data `status` di level teratas adalah `String` ("success"), bukan `Boolean`. Itulah kenapa di Android kebaca `false` dan tokennya `null`.

## Analisa Masalah
Struktur JSON asli dari backend kamu:
```json
{
  "data": {
    "status": true,
    "message": "Login Successfully",
    "data": { // Ini tempat access_token asli
      "access_token": "...",
      ...
    }
  },
  "message": "Login successfully",
  "status": "success"
}
```
Sedangkan kodingan kita sekarang cuma ngarep satu level `data`.

## Perubahan yang Diusulkan

### Data Transfer Object (DTO)

#### [MODIFY] [LoginResponse.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/response/LoginResponse.kt)
- Membuat class `LoginNestedData` untuk menangani pembungkus level pertama.
- Mengubah `status` di level teratas menjadi `String`.
- Menyesuaikan hirarki agar `AuthData` diambil dari `data.data`.

### Business Logic

#### [MODIFY] [LoginViewModel.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/login/LoginViewModel.kt)
- Mengubah logika pengecekan sukses: `response.status == "success"` dan `response.data?.status == true`.
- Mengambil token dari `response.data?.data?.accesstoken`.

## Verifikasi Plan
- Melakukan build project.
- Mengecek Logcat untuk memastikan `Response Status` sekarang terbaca `success` dan token tidak lagi `null`.
