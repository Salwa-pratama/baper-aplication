package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.request.LoginRequest
import com.example.baper_andoid.data.remote.dto.request.RegisterRequest
import com.example.baper_andoid.data.remote.dto.response.LoginResponse
import com.example.baper_andoid.data.remote.dto.response.RegisterResponse

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(email, password))
    }

    suspend fun register(name: String, email: String,phone : String,  password: String): RegisterResponse {
        return apiService.register(RegisterRequest(name, email, phone, password))
    }
}