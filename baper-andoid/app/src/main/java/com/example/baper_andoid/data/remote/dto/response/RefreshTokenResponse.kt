package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenData(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?
)

data class RefreshTokenResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: RefreshTokenData?
)
