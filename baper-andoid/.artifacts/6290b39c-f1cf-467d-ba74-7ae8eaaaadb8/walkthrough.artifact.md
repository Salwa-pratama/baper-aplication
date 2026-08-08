# Walkthrough - Webhook (Chat) Integration

Fitur pengiriman pesan teks dan media melalui Meta/WhatsApp Integration telah berhasil diimplementasikan di sisi Android.

## Perubahan Utama

### 1. Data Transfer Objects (DTO)
Dibuat DTO yang sesuai dengan struktur JSON dari backend Go:
- **[SendMessageRequest.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/request/SendMessageRequest.kt):** Field `to` dan `msg`.
- **[SendMediaRequest.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/request/SendMediaRequest.kt):** Field `to`, `media_url`, `type`, dan `caption`.
- **[ChatResponse.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/response/ChatResponse.kt):** Response generic dengan `status`, `message`, dan `data`.

### 2. Service Layer
Update pada **[ApiService.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/ApiService.kt)** dengan endpoint:
- `POST api/webhook/send-message`
- `POST api/webhook/send-media`

### 3. Repository Layer
Dibuat **[ChatRepository.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/repository/ChatRepository.kt)** untuk mempermudah pemanggilan fungsi chat dari ViewModel.

## Verifikasi yang Dilakukan
- Memastikan field `@SerializedName` sinkron dengan backend (misal: `media_url`).
- Verifikasi struktur folder dan package agar tetap konsisten dengan project.
- Pengecekan import dan parameter fungsi di Repository.

> [!NOTE]
> Fitur `ReceiveMessage` dan `UploadMedia` tidak diimplementasikan di sisi Android karena merupakan logika internal backend atau memerlukan penanganan file yang lebih spesifik jika nantinya dibutuhkan di UI.
