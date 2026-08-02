package com.example.baper_andoid.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.repository.AuthRepository
import com.example.baper_andoid.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token : String) : LoginState()
    data class Error(val message: String ) : LoginState()
}




class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState : StateFlow<LoginState> = _loginState.asStateFlow()


    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authRepository.login(email, password)
                if (response.status && response.token != null) {
                    _loginState.value = LoginState.Success(response.token)
                } else {
                    _loginState.value = LoginState.Error(response.message)
                }
            } catch (e : Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Terjadi kesalahan, Mohon tunggu beberapa saat")
            }
        }
    }
}