# Walkthrough - Real-time Chat & Data Synchronization

Fitur chat sekarang telah ditingkatkan untuk memberikan pengalaman yang lebih "nyata" (real-time) dan data yang selalu sinkron antara halaman Home dan Chat.

## Perubahan Utama

### 1. Pengiriman Pesan Instan & Sinkronisasi Home
Sekarang, saat Anda mengirim pesan di layar chat:
- **Bubble Chat:** Pesan langsung muncul di layar chat tanpa menunggu loading API selesai.
- **Home Screen Sync:** Jika Anda kembali ke halaman Beranda, pesan terakhir pada daftar obrolan akan otomatis terupdate dengan pesan yang baru Anda kirim tadi.

### 2. Auto-Polling (Simulasi Real-time)
Karena aplikasi menggunakan REST API (bukan WebSocket), saya telah menambahkan fitur **Auto-Polling** pada [ChatScreen.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/chat/ChatScreen.kt):
- Aplikasi akan otomatis mengecek pesan baru dari backend setiap **5 detik** saat Anda membuka percakapan.
- Jika pelanggan membalas via WhatsApp, pesan tersebut akan muncul secara otomatis di bubble chat tanpa perlu refresh manual.

### 3. Set Data Instan (No More "Memuat...")
Saya menambahkan fungsi `setActiveConversation` di [ChatViewModel.kt](file:///media/pratama/Data1/FTI/baper/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/chat/ChatViewModel.kt).
- Begitu Anda mengklik salah satu chat di Beranda, nama pelanggan langsung dikirim ke layar chat.
- **Hasilnya:** Nama di Top Bar muncul seketika, dan Anda bisa langsung mengetik serta mengirim pesan tanpa harus menunggu API utama selesai memuat data.

### 4. Integrasi Refresh yang Lebih Cepat
Indikator swipe-to-refresh sekarang terhubung langsung dengan status loading API yang sebenarnya. Begitu backend selesai mengirim data, lingkaran loading akan langsung hilang.

## Verifikasi yang Dilakukan
- **Build Success:** Project berhasil dikompilasi dengan lancar.
- **Sync Test:** Memastikan daftar `_chatList` di ViewModel diperbarui secara reaktif menggunakan `mutableStateListOf`.
- **Polling Test:** Memastikan perulangan polling berjalan di dalam scope `LaunchedEffect` yang aman (akan mati otomatis saat Anda keluar dari layar chat).

> [!TIP]
> Dengan adanya Auto-Polling, user tidak perlu sering-sering melakukan swipe-to-refresh secara manual untuk melihat balasan dari pelanggan.
