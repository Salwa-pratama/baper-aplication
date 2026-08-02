package com.example.baper_andoid.ui.screen.login

import android.util.Log
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
                Log.d("LoginViewModel", "Top Level Status: ${response.status}")
                Log.d("LoginViewModel", "Nested Data Status: ${response.data?.status}")
                Log.d("LoginViewModel", "Token exists: ${response.data?.data?.accesstoken != null}")

                val nestedData = response.data
                val authData = nestedData?.data

                if (response.status == "success" && nestedData?.status == true && authData?.accesstoken != null) {
                    _loginState.value = LoginState.Success(authData.accesstoken)
                    Log.d("LoginViewModel", "Login Success! Token extracted.")
                } else {
                    val errorMsg = when {
                        response.status != "success" -> response.message
                        nestedData?.status == false -> nestedData.message
                        authData?.accesstoken == null -> "Token tidak ditemukan dalam payload data"
                        else -> response.message
                    }
                    _loginState.value = LoginState.Error(errorMsg)
                    Log.e("LoginViewModel", "Login Error: $errorMsg")
                }
            } catch (e : Exception) {
                val errorMsg = e.localizedMessage ?: "Terjadi kesalahan koneksi"
                _loginState.value = LoginState.Error(errorMsg)
                Log.e("LoginViewModel", "Exception: $errorMsg", e)
            }
        }
    }
}