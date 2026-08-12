package com.example.baper_andoid.ui.screen.produk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baper_andoid.data.repository.ProductRepository

class ProdukViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProdukViewModel::class.java)) {
            return ProdukViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
