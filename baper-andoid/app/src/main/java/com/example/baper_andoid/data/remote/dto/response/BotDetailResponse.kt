package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class BotDetailData(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("agent_prompt")
    val agentPrompt: String,
    @SerializedName("agent_api")
    val agentApi: String
)

data class BotDetailResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: BotDetailData?
)
