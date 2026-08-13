package com.example.baper_andoid.ui.screen.lihatpesanan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baper_andoid.data.repository.OrderRepository

class LihatPesananViewModelFactory(private val repository: OrderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LihatPesananViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LihatPesananViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
