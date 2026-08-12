# Implementation Plan - Chat Real-time Experience & Data Sync

Memperbaiki sinkronisasi chat agar pesan yang dikirim langsung muncul, nama kontak tampil seketika, dan data di halaman Home tetap sinkron dengan percakapan terakhir.

## User Review Required

> [!NOTE]
> - **Simulasi Real-time:** Karena backend belum mendukung WebSocket/Push Notification, saya akan menerapkan **Auto-Polling** (mengambil data setiap 5 detik) saat user berada di layar chat untuk memberikan efek "real-time".
> - **Instant Feedback:** Pesan yang dikirim akan langsung muncul di UI secara lokal sebelum konfirmasi dari server selesai.

## Proposed Changes

### [Data Layer]

#### [MODIFY] [ChatViewModel.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/chat/ChatViewModel.kt)
- Menambahkan fungsi `setActiveConversation(item: ConversationItem)` untuk mengisi nama dan nomor HP secara instan saat navigasi dimulai.
- Memperbaiki logika `sendMessage` agar memperbarui `_chatList` (halaman Home) dan `_messages` (halaman Chat) secara sinkron.
- Menghapus pengecekan `toPhone.isBlank()` yang menghambat pengiriman jika data API belum selesai dimuat.

### [UI Layer]

#### [MODIFY] [HomeScreen.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/home/HomeScreen.kt)
- Memanggil `chatViewModel.setActiveConversation(chat)` saat card chat diklik sebelum navigasi dilakukan.
- Memastikan `isRefreshing` terhubung dengan `fetchConversations`.

#### [MODIFY] [ChatScreen.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/chat/ChatScreen.kt)
- Implementasi **Auto-Polling** menggunakan `LaunchedEffect` dan `delay` untuk mengecek pesan baru setiap 5 detik.
- Memastikan `PullToRefreshBox` melakukan refresh penuh (clearing state).

## Verification Plan

### Manual Verification
1. Klik salah satu chat di Home, pastikan nama di Top Bar langsung muncul tanpa menunggu "Memuat...".
2. Kirim pesan, pastikan bubble langsung muncul di list.
3. Kembali ke Home, pastikan "Pesan Terakhir" di card chat sudah terupdate dengan pesan yang baru saja dikirim.
4. Diamkan layar chat, pastikan pesan balasan dari pelanggan (jika ada) muncul otomatis dalam hitungan detik.
