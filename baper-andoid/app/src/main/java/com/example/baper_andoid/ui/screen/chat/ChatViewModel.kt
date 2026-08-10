package com.example.baper_andoid.ui.screen.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel : ViewModel() {
    // State untuk daftar chat di Home
    private val _chatList = mutableStateListOf<BaperChat>().apply { addAll(getMockChats()) }
    val chatList: List<BaperChat> get() = _chatList

    // State untuk percakapan di dalam chat (untuk demo kita pakai 1 list saja)
    private val _messages = mutableStateListOf<ChatMessage>().apply { addAll(getInitialMessages()) }
    val messages: List<ChatMessage> get() = _messages

    fun markAsRead(chatId: String) {
        val index = _chatList.indexOfFirst { it.id == chatId }
        if (index != -1) {
            val chat = _chatList[index]
            if (chat.unreadCount > 0) {
                _chatList[index] = chat.copy(unreadCount = 0)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        
        // 1. Tambah ke daftar pesan
        _messages.add(ChatMessage(text, true, currentTime))
        
        // 2. Update info "pesan terakhir" di Home untuk chat yang sedang dibuka (misal ID 1)
        val index = _chatList.indexOfFirst { it.id == "1" }
        if (index != -1) {
            _chatList[index] = _chatList[index].copy(
                lastMessage = text,
                time = currentTime
            )
        }
    }
}
