package com.example.baper_andoid.ui.screen.lihatpesanan

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.remote.dto.response.OrderResponse
import com.example.baper_andoid.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.baper_andoid.utils.getErrorMessage

class LihatPesananViewModel(private val repository: OrderRepository) : ViewModel() {
    private val _orders = mutableStateListOf<OrderResponse>()
    val orders: List<OrderResponse> get() = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getOrders()
                if (response.status) {
                    _orders.clear()
                    if (response.data != null) {
                        _orders.addAll(response.data)
                    }
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.getErrorMessage("Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.confirmPayment(orderId)
                if (response.status) {
                    getOrders() // Refresh data from backend after success
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.getErrorMessage("Gagal mengonfirmasi pesanan")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
