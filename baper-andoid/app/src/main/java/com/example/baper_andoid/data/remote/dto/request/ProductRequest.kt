package com.example.baper_andoid.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * business_id sengaja TIDAK dikirim: backend menentukannya dari JWT.
 * Kalau dikirim dari klien, user bisa menitipkan produk ke bisnis orang lain.
 */
data class ProductRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("stock")
    val stock: Int
)
