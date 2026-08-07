package com.example.baper_andoid.data.remote.dto.request

data class RegisterRequest(
    val business_address: String,
    val business_description: String,
    val business_name: String,
    val business_phone: String,
    val email: String,
    val name: String,
    val password: String
)