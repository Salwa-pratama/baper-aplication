package com.example.baper_andoid.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateBotRequest(
    @SerializedName("agent_api")
    val agentApi: String,
    @SerializedName("agent_prompt")
    val agentPrompt: String
)
