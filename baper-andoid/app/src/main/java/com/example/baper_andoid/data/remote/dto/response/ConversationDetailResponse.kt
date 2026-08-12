package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class MessageItem(
    @SerializedName("id") val id: String,
    @SerializedName("sender_type") val senderType: String,
    @SerializedName("content") val content: String,
    @SerializedName("metadata") val metadata: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("session_id") val sessionId: String
)

data class ConversationDetailItem(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("customer_phone") val customerPhone: String,
    @SerializedName("status") val status: String,
    @SerializedName("messages") val messages: List<MessageItem>
)

data class ConversationDetailResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ConversationDetailItem
)
