package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.request.UpdateBotRequest
import com.example.baper_andoid.data.remote.dto.response.UpdateBotResponse

class BotRepository(private val apiService: ApiService) {

    suspend fun updateBotPrompt(
        id: String,
        agentApi: String,
        agentPrompt: String
    ): UpdateBotResponse {
        val request = UpdateBotRequest(
            agentApi = agentApi,
            agentPrompt = agentPrompt
        )
        return apiService.updateBotPrompt(id, request)
    }
}
