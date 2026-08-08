package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.request.SendMessageRequest
import com.example.baper_andoid.data.remote.dto.request.SendMediaRequest
import com.example.baper_andoid.data.remote.dto.response.ChatResponse

class ChatRepository(private val apiService: ApiService) {

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
