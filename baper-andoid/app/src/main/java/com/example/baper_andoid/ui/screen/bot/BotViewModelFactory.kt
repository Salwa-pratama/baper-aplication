package com.example.baper_andoid.ui.screen.bot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baper_andoid.data.repository.BotRepository

class BotViewModelFactory(private val repository: BotRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BotViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
