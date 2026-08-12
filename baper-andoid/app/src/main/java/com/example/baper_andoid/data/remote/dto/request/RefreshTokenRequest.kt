package com.example.baper_andoid.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * Dipakai untuk POST /api/auth/refresh — menukar refresh token
 * yang masih berlaku dengan access token baru.
 */
data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)
