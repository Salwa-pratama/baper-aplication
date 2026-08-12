package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ConversationItem(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("bot_id") val botId: String,
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("status") val status: String,
    @SerializedName("started_at") val startedAt: String,
    @SerializedName("ended_at") val endedAt: String?,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("customer_phone") val customerPhone: String,
    @SerializedName("last_message") val lastMessage: String,
    @SerializedName("last_message_sender") val lastMessageSender: String,
    @SerializedName("last_message_at") val lastMessageAt: String?,
    @SerializedName("message_count") val messageCount: Int
)

data class ConversationListResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<ConversationItem>
)
