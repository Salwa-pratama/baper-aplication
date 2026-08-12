package com.example.baper_andoid.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(
        fullName: String,
        email: String,
        password: String,
        businessName: String,
        businessType: String,
        businessAddress: String,
        businessPhone: String
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = authRepository.register(
                    businessAddress = businessAddress,
                    businessDescription = businessType,
                    businessName = businessName,
                    businessPhone = businessPhone,
                    email = email,
                    name = fullName,
                    password = password
                )
                if (response.status) {
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error(response.message)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.localizedMessage ?: "Pendaftaran gagal")
            }
        }
    }
}
