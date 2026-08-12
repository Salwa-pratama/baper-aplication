package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService
import com.example.baper_andoid.data.remote.dto.request.ProductRequest
import com.example.baper_andoid.data.remote.dto.response.ProductDetailResponse
import com.example.baper_andoid.data.remote.dto.response.ProductListResponse
import com.example.baper_andoid.data.remote.dto.response.ProductResponse

/**
 * businessId tidak lagi jadi parameter: backend mengambilnya dari token.
 */
class ProductRepository(private val apiService: ApiService) {

    suspend fun getProducts(): ProductListResponse {
        return apiService.getProducts()
    }

    suspend fun createProduct(
        name: String,
        description: String,
        price: Int,
        stock: Int
    ): ProductResponse {
        val request = ProductRequest(
            name = name,
            description = description,
            price = price,
            stock = stock
        )
        return apiService.createProduct(request)
    }

    suspend fun getProductById(id: String): ProductDetailResponse {
        return apiService.getProductById(id)
    }

    suspend fun updateProduct(
        id: String,
        name: String,
        description: String,
        price: Int,
        stock: Int
    ): ProductResponse {
        val request = ProductRequest(
            name = name,
            description = description,
            price = price,
            stock = stock
        )
        return apiService.updateProduct(id, request)
    }

    suspend fun deleteProduct(id: String): ProductResponse {
        return apiService.deleteProduct(id)
    }
}
