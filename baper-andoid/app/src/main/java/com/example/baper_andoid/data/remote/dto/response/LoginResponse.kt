package com.example.baper_andoid.data.remote.dto.response

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val token: String?,
    val user: UserResponse?
)