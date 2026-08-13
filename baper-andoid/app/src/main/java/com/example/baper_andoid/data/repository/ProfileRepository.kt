package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.response.ProfileResponse

class ProfileRepository(private val apiService: ApiService) {
    suspend fun getProfile(): ProfileResponse {
        return apiService.getProfile()
    }
}
