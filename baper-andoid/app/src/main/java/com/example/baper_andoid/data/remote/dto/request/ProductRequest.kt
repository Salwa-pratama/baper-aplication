package com.example.baper_andoid.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ProductRequest(
    @SerializedName("business_id")
    val businessId: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("stock")
    val stock: Int
)
