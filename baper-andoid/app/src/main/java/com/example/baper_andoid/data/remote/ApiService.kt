package com.example.baper_andoid.data.remote

import com.example.baper_andoid.data.remote.dto.request.LoginRequest
import com.example.baper_andoid.data.remote.dto.request.RefreshTokenRequest
import com.example.baper_andoid.data.remote.dto.request.RegisterRequest
import com.example.baper_andoid.data.remote.dto.request.UpdateBotRequest
import com.example.baper_andoid.data.remote.dto.request.ProductRequest
import com.example.baper_andoid.data.remote.dto.request.SendMessageRequest
import com.example.baper_andoid.data.remote.dto.request.SendMediaRequest
import com.example.baper_andoid.data.remote.dto.response.BotResponse
import com.example.baper_andoid.data.remote.dto.response.BotDetailResponse
import com.example.baper_andoid.data.remote.dto.response.LoginResponse
import com.example.baper_andoid.data.remote.dto.response.RefreshTokenResponse
import com.example.baper_andoid.data.remote.dto.response.RegisterResponse
import com.example.baper_andoid.data.remote.dto.response.ProductResponse
import com.example.baper_andoid.data.remote.dto.response.ProductListResponse
import com.example.baper_andoid.data.remote.dto.response.ProductDetailResponse
import com.example.baper_andoid.data.remote.dto.response.ChatResponse
import com.example.baper_andoid.data.remote.dto.response.ConversationListResponse
import com.example.baper_andoid.data.remote.dto.response.ConversationDetailResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * BASE_URL sudah berakhir dengan "/api/", jadi path di sini TIDAK boleh
 * diawali "api/" lagi — kalau tidak URL-nya jadi /api/api/products (404).
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse

    @GET("bots/mine")
    suspend fun getMyBot(): BotDetailResponse

    @PUT("bots/{id}/prompt")
    suspend fun updateBotPrompt(
        @Path("id") id: String,
        @Body request: UpdateBotRequest
    ): BotResponse

    @PATCH("bots/{id}/toggle")
    suspend fun toggleBotStatus(
        @Path("id") id: String
    ): BotResponse

    // Product CRUD.
    // business_id tidak lagi dikirim: backend mengambilnya dari JWT
    // supaya user tidak bisa membaca/menulis produk bisnis orang lain.
    @GET("products")
    suspend fun getProducts(): ProductListResponse

    @POST("products")
    suspend fun createProduct(
        @Body request: ProductRequest
    ): ProductResponse

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): ProductDetailResponse

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body request: ProductRequest
    ): ProductResponse

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String
    ): ProductResponse

    // Webhook / Chat — butuh Bearer token.
    @GET("conversations")
    suspend fun getConversations(): ConversationListResponse

    @GET("conversations/{id}/messages")
    suspend fun getConversationMessages(
        @Path("id") id: String
    ): ConversationDetailResponse

    @PATCH("conversations/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: String
    ): ChatResponse

    @DELETE("conversations/{id}")
    suspend fun deleteConversation(
        @Path("id") id: String
    ): ChatResponse

    @PATCH("conversations/{id}/block")
    suspend fun blockCustomer(
        @Path("id") id: String
    ): ChatResponse

    @POST("webhook/send-message")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): ChatResponse

    @POST("webhook/send-media")
    suspend fun sendMedia(
        @Body request: SendMediaRequest
    ): ChatResponse

    @retrofit2.http.Multipart
    @POST("webhook/upload-media")
    suspend fun uploadMedia(
        @retrofit2.http.Part("to") to: okhttp3.RequestBody,
        @retrofit2.http.Part("type") type: okhttp3.RequestBody,
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part,
        @retrofit2.http.Part("caption") caption: okhttp3.RequestBody?
    ): ChatResponse

    // Orders
    @GET("orders")
    suspend fun getOrders(): com.example.baper_andoid.data.remote.dto.response.OrderListResponse

    @PATCH("orders/{id}/status")
    suspend fun confirmPayment(
        @Path("id") id: String
    ): com.example.baper_andoid.data.remote.dto.response.OrderActionResponse

    @GET("profile")
    suspend fun getProfile(): com.example.baper_andoid.data.remote.dto.response.ProfileResponse

    @retrofit2.http.Streaming
    @GET("business/recap/monthly/export")
    suspend fun exportMonthlyRecap(
        @retrofit2.http.Query("year") year: Int,
        @retrofit2.http.Query("month") month: Int
    ): okhttp3.ResponseBody
}
