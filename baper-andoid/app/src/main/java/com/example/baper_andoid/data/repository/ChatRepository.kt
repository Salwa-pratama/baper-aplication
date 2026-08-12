package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.request.SendMessageRequest
import com.example.baper_andoid.data.remote.dto.request.SendMediaRequest
import com.example.baper_andoid.data.remote.dto.response.ChatResponse
import com.example.baper_andoid.data.remote.dto.response.ConversationListResponse
import com.example.baper_andoid.data.remote.dto.response.ConversationDetailResponse

class ChatRepository(private val apiService: ApiService) {

    suspend fun getConversations(): ConversationListResponse {
        return apiService.getConversations()
    }

    suspend fun getConversationMessages(sessionId: String): ConversationDetailResponse {
        return apiService.getConversationMessages(sessionId)
    }

    suspend fun sendMessage(to: String, msg: String): ChatResponse {
        val request = SendMessageRequest(to = to, msg = msg)
        return apiService.sendMessage(request)
    }

    suspend fun sendMedia(
        to: String,
        mediaUrl: String,
        mediaType: String,
        caption: String
    ): ChatResponse {
        val request = SendMediaRequest(
            to = to,
            mediaUrl = mediaUrl,
            type = mediaType,
            caption = caption
        )
        return apiService.sendMedia(request)
    }
}
