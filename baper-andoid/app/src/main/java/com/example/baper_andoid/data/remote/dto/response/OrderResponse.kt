package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("customerName")
    val customerName: String,
    @SerializedName("packageName")
    val packageName: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("chatId")
    val chatId: String
)

data class OrderListResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<OrderResponse>
)

data class OrderActionResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Any?
)
