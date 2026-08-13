package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("business_name") val businessName: String,
    @SerializedName("joined_date") val joinedDate: String,
    @SerializedName("total_orders") val totalOrders: Int,
    @SerializedName("total_customers") val totalCustomers: Int
)

data class ProfileResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ProfileData?
)
