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

    suspend fun uploadMedia(
        to: String,
        type: String,
        file: okhttp3.MultipartBody.Part,
        caption: String?
    ): ChatResponse {
        val toBody = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, to)
        val typeBody = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, type)
        val captionBody = caption?.let { okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, it) }
        
        return apiService.uploadMedia(toBody, typeBody, file, captionBody)
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

    suspend fun markAsRead(sessionId: String): ChatResponse {
        return apiService.markAsRead(sessionId)
    }

    suspend fun deleteConversation(sessionId: String): ChatResponse {
        return apiService.deleteConversation(sessionId)
    }

    suspend fun blockCustomer(sessionId: String): ChatResponse {
        return apiService.blockCustomer(sessionId)
    }
}
