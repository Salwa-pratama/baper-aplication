package com.example.baper_andoid.data.remote

import com.example.baper_andoid.data.remote.dto.request.LoginRequest
import com.example.baper_andoid.data.remote.dto.request.RegisterRequest
import com.example.baper_andoid.data.remote.dto.request.UpdateBotRequest
import com.example.baper_andoid.data.remote.dto.response.LoginResponse
import com.example.baper_andoid.data.remote.dto.response.RegisterResponse
import com.example.baper_andoid.data.remote.dto.response.UpdateBotResponse
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @PUT("api/bots/{id}/prompt")
    suspend fun updateBotPrompt(
        @Path("id") id: String,
        @Body request: UpdateBotRequest
    ): UpdateBotResponse

    @GET("api/stores")
    suspend fun getAllStores(): List<Any> // ganti sesuai response backend kamu
}