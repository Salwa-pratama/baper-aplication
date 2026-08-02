package com.example.baper_andoid.data.remote.dto.request

data class RegisterRequest(
    val firstname : String,
    val lastname : String,
    val email : String,
    val phone : String,
    val password : String
)