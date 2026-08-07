package com.example.baper_andoid.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: RegisterData
)

data class RegisterData(
    @SerializedName("id_user")
    val idUser: String,
    @SerializedName("name")
    val name: String
)
