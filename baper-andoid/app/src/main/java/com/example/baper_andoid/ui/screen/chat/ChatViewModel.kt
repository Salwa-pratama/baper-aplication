package com.example.baper_andoid.ui.screen.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.repository.ChatRepository
import com.example.baper_andoid.data.remote.dto.response.ConversationItem
import com.example.baper_andoid.data.remote.dto.response.MessageItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    
    // State untuk daftar chat di Home
    private val _chatList = mutableStateListOf<ConversationItem>()
    val chatList: List<ConversationItem> get() = _chatList

    // State untuk percakapan di dalam chat
    private val _messages = mutableStateListOf<MessageItem>()
    val messages: List<MessageItem> get() = _messages
    
    private val _currentCustomerPhone = MutableStateFlow("")
    val currentCustomerPhone: StateFlow<String> get() = _currentCustomerPhone

    private val _currentCustomerName = MutableStateFlow("")
    val currentCustomerName: StateFlow<String> = _currentCustomerName.asStateFlow()

    /** Set data pelanggan secara instan saat navigasi dimulai dari Home */
    fun setActiveConversation(item: ConversationItem) {
        _currentCustomerName.value = item.customerName
        _currentCustomerPhone.value = item.customerPhone
    }

    fun fetchConversations(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.getConversations()
                if (response.status) {
                    _chatList.clear()
                    _chatList.addAll(response.data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onComplete()
            }
        }
    }

    fun fetchMessages(sessionId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.getConversationMessages(sessionId)
                if (response.status) {
                    // Update only if data is actually different or to refresh the list
                    _messages.clear()
                    _messages.addAll(response.data.messages)
                    _currentCustomerPhone.value = response.data.customerPhone
                    _currentCustomerName.value = response.data.customerName
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onComplete()
            }
        }
    }

    fun markAsRead(chatId: String) {
        // Optional: Implement if API supports marking as read.
    }

    fun sendMessage(text: String, sessionId: String) {
        if (text.isBlank()) return
        val toPhone = _currentCustomerPhone.value
        
        // Tetap coba kirim jika phone belum terisi (biasanya sudah di-set dari setActiveConversation)
        viewModelScope.launch {
            try {
                val response = repository.sendMessage(toPhone, text)
                if (response.status) {
                    val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }.format(Date())
                    
                    // 1. Update List Pesan (Real-time di Chat Screen)
                    _messages.add(
                        MessageItem(
                            id = UUID.randomUUID().toString(),
                            senderType = "bot", 
                            content = text,
                            metadata = "",
                            createdAt = currentTime,
                            sessionId = sessionId
                        )
                    )

                    // 2. Update List Chat (Real-time di Home Screen)
                    val index = _chatList.indexOfFirst { it.sessionId == sessionId }
                    if (index != -1) {
                        val updatedItem = _chatList[index].copy(
                            lastMessage = text,
                            lastMessageAt = currentTime,
                            lastMessageSender = "bot"
                        )
                        _chatList[index] = updatedItem
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
