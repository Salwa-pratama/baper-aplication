package com.example.baper_andoid.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(
    @SerializedName("to")
    val to: String,
    @SerializedName("msg")
    val msg: String
)
