package com.example.baper_andoid.ui.screen.rekap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baper_andoid.data.repository.OrderRepository

class RekapViewModelFactory(private val repository: OrderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RekapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RekapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
