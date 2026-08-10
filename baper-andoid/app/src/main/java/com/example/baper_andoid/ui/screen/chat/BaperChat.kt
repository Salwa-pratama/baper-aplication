package com.example.baper_andoid.ui.screen.chat

data class BaperChat(
    val id: String,
    val name: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0
)

fun getMockChats() = listOf(
    BaperChat("1", "Siti Reseller - Bandung", "Halo min, orderan Paket A 5 unit ready?", "09:12", 2),
    BaperChat("2", "Agus Sembako", "Siap, sudah saya transfer ya mas tadi siang", "Kemarin"),
    BaperChat("3", "Budi Gede Toko", "Minta rekapan totalan belanja seminggu ini ya", "Kemarin", 1),
    BaperChat("4", "Dedi Kurniawan", "Siap gan, langsung order.", "Kemarin")
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val time: String
)

fun getInitialMessages() = listOf(
    ChatMessage("Halo! Selamat datang di layanan bantuan BAPER. Ada yang bisa kami bantu?", false, "10:00"),
    ChatMessage("Saya mau tanya status pengiriman pesanan #BPR123", true, "10:01"),
    ChatMessage("Baik kak, pesanan Anda sedang dalam proses packing dan akan segera dikirim sore ini.", false, "10:02")
)
