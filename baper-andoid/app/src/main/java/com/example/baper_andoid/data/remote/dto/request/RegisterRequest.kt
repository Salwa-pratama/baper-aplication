package com.example.baper_andoid.data.remote.dto.request

data class RegisterRequest(
    val name : String,
    val email : String,
    val phone : String,
    val password : String
)