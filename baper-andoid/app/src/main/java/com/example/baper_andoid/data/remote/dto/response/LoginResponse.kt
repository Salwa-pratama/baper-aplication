package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

data class AuthData(
    @SerializedName("access_token")
    val accesstoken: String?,
    @SerializedName("refresh_token")
    val refreshtoken: String?,
    @SerializedName("user")
    val user: User?
)



data class LoginResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: AuthData?
)
