package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class UpdateBotResponse(
    @SerializedName("data")
    val data: String?,
    @SerializedName("message")
    val message: String
)
