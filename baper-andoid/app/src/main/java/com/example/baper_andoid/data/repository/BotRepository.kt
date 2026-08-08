package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.request.UpdateBotRequest
import com.example.baper_andoid.data.remote.dto.response.BotResponse

class BotRepository(private val apiService: ApiService) {

    suspend fun updateBotPrompt(
        id: String,
        agentApi: String,
        agentPrompt: String
    ): BotResponse {
        val request = UpdateBotRequest(
            agentApi = agentApi,
            agentPrompt = agentPrompt
        )
        return apiService.updateBotPrompt(id, request)
    }

    suspend fun toggleBotStatus(id: String): BotResponse {
        return apiService.toggleBotStatus(id)
    }
}
