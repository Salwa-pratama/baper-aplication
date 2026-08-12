package com.example.baper_andoid.data.remote

import android.content.Context
import com.example.baper_andoid.data.local.UserPreferences
import com.example.baper_andoid.data.remote.dto.request.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.4:3000/api/"

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /** Menempelkan "Authorization: Bearer <access token>" pada setiap request. */
    private fun authInterceptor(prefs: UserPreferences) = Interceptor { chain ->
        val token = runBlocking { prefs.getAuthTokenOnce() }
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    /**
     * Kalau server menjawab 401, coba tukar refresh token sekali lalu ulangi
     * request dengan access token yang baru. Backend memutar refresh token
     * (mengembalikan yang baru), jadi keduanya disimpan ulang.
     */
    private fun tokenRefreshInterceptor(
        prefs: UserPreferences,
        refreshApi: () -> ApiService
    ) = Interceptor { chain ->
        val response = chain.proceed(chain.request())

        if (response.code != 401) return@Interceptor response

        // Jangan mencoba refresh untuk endpoint auth itu sendiri.
        val path = chain.request().url.encodedPath
        if (path.contains("/auth/")) return@Interceptor response

        val refresh = runBlocking { prefs.getRefreshTokenOnce() }
        if (refresh.isNullOrEmpty()) return@Interceptor response

        val newAccessToken: String? = runBlocking {
            try {
                val result = refreshApi().refreshToken(RefreshTokenRequest(refresh))
                val data = result.data
                if (result.status && !data?.accessToken.isNullOrEmpty()) {
                    prefs.saveTokens(data!!.accessToken!!, data.refreshToken)
                    data.accessToken
                } else {
                    prefs.clearSession()
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        if (newAccessToken.isNullOrEmpty()) return@Interceptor response

        response.close()
        val retried: Request = chain.request().newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
        chain.proceed(retried)
    }

    /** Client tanpa auth — dipakai login, register, dan refresh itu sendiri. */
    private fun plainRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Instance untuk login/register/refresh (tidak butuh token). */
    val instance: ApiService by lazy {
        plainRetrofit().create(ApiService::class.java)
    }

    /** Instance untuk endpoint yang butuh token. */
    fun getInstance(context: Context): ApiService {
        val prefs = UserPreferences(context.applicationContext)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .addInterceptor(authInterceptor(prefs))
            .addInterceptor(tokenRefreshInterceptor(prefs) { instance })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
