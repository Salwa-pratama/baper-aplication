# Implementation Plan - Webhook (Chat) Integration

Implementasi fitur pengiriman pesan dan media melalui backend (Meta/WhatsApp Integration) berdasarkan spesifikasi Go backend dan Swagger.

## Proposed Changes

### [Data Layer]

#### [NEW] [SendMessageRequest.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/request/SendMessageRequest.kt)
DTO untuk mengirim pesan teks.
- `to`: String
- `msg`: String

#### [NEW] [SendMediaRequest.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/request/SendMediaRequest.kt)
DTO untuk mengirim media.
- `to`: String
- `media_url`: String
- `type`: String (image, document, video, audio)
- `caption`: String

#### [NEW] [ChatResponse.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/dto/response/ChatResponse.kt)
DTO response generic untuk fitur chat.
- `status`: String
- `message`: String
- `data`: Any?

#### [MODIFY] [ApiService.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/remote/ApiService.kt)
Menambahkan endpoint:
- `POST api/webhook/send-message`
- `POST api/webhook/send-media`

#### [NEW] [ChatRepository.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/data/repository/ChatRepository.kt)
Repository untuk menangani pengiriman pesan dan media.

## Verification Plan

### Manual Verification
- Verifikasi kode build dengan sukses.
- Memastikan mapping field `@SerializedName` sudah sesuai dengan struct Go backend.
