package com.example.baper_andoid.data.remote

import com.example.baper_andoid.data.remote.dto.request.LoginRequest
import com.example.baper_andoid.data.remote.dto.request.RegisterRequest
import com.example.baper_andoid.data.remote.dto.request.UpdateBotRequest
import com.example.baper_andoid.data.remote.dto.request.ProductRequest
import com.example.baper_andoid.data.remote.dto.request.SendMessageRequest
import com.example.baper_andoid.data.remote.dto.request.SendMediaRequest
import com.example.baper_andoid.data.remote.dto.response.BotResponse
import com.example.baper_andoid.data.remote.dto.response.LoginResponse
import com.example.baper_andoid.data.remote.dto.response.RegisterResponse
import com.example.baper_andoid.data.remote.dto.response.ProductResponse
import com.example.baper_andoid.data.remote.dto.response.ProductListResponse
import com.example.baper_andoid.data.remote.dto.response.ProductDetailResponse
import com.example.baper_andoid.data.remote.dto.response.ChatResponse
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @PUT("api/bots/{id}/prompt")
    suspend fun updateBotPrompt(
        @Path("id") id: String,
        @Body request: UpdateBotRequest
    ): BotResponse

    @PATCH("api/bots/{id}/toggle")
    suspend fun toggleBotStatus(
        @Path("id") id: String
    ): BotResponse

    // Product CRUD
    @GET("api/products")
    suspend fun getProducts(
        @Query("business_id") businessId: String
    ): ProductListResponse

    @POST("api/products")
    suspend fun createProduct(
        @Body request: ProductRequest
    ): ProductResponse

    @GET("api/products/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): ProductDetailResponse

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body request: ProductRequest
    ): ProductResponse

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String
    ): ProductResponse

    // Webhook / Chat
    @POST("api/webhook/send-message")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): ChatResponse

    @POST("api/webhook/send-media")
    suspend fun sendMedia(
        @Body request: SendMediaRequest
    ): ChatResponse

    @GET("api/stores")
    suspend fun getAllStores(): List<Any> // ganti sesuai response backend kamu
}