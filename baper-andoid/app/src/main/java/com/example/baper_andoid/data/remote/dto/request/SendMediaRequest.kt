package com.example.baper_andoid.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class SendMediaRequest(
    @SerializedName("to")
    val to: String,
    @SerializedName("media_url")
    val mediaUrl: String,
    @SerializedName("type")
    val type: String, // image, document, video, audio
    @SerializedName("caption")
    val caption: String
)
