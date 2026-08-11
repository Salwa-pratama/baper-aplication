package com.example.baper_andoid.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baper_andoid.data.local.UserPreferences
import com.example.baper_andoid.data.repository.AuthRepository

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
