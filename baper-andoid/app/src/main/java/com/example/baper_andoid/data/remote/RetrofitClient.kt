package com.example.baper_andoid.data.remote

import android.content.Context
import com.example.baper_andoid.data.local.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.100.124:3000/api/"

    private fun getLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun getAuthInterceptor(context: Context) = Interceptor { chain ->
        val userPreferences = UserPreferences(context)
        val token = runBlocking { userPreferences.authToken.first() }
        
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    // Gunakan fungsi ini untuk inisialisasi instance dengan Context
    fun getInstance(context: Context): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(getAuthInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Instance legacy untuk login/register yang tidak butuh token
    val instance: ApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(getLoggingInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
