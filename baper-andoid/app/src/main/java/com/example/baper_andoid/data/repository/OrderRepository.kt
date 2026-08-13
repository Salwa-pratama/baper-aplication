package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.response.OrderActionResponse
import com.example.baper_andoid.data.remote.dto.response.OrderListResponse
import com.example.baper_andoid.data.remote.dto.response.OrderResponse

class OrderRepository(private val apiService: ApiService) {

    suspend fun getOrders(): OrderListResponse {
        return apiService.getOrders()
    }

    suspend fun confirmPayment(id: String): OrderActionResponse {
        return apiService.confirmPayment(id)
    }

    suspend fun exportMonthlyRecap(year: Int, month: Int): okhttp3.ResponseBody {
        return apiService.exportMonthlyRecap(year, month)
    }
}
