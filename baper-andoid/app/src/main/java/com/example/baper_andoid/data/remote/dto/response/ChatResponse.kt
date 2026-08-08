package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Any?
)
