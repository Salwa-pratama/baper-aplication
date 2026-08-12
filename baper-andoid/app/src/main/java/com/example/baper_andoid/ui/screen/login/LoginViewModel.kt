package com.example.baper_andoid.ui.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.local.UserPreferences
import com.example.baper_andoid.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authRepository.login(email, password)
                val authData = response.data
                val accessToken = authData?.accesstoken

                if (response.status && !accessToken.isNullOrEmpty()) {
                    // Simpan access token + refresh token + identitas user.
                    // Refresh token dipakai OkHttp interceptor saat access
                    // token kedaluwarsa (72 jam), supaya user tidak perlu
                    // login ulang setiap kali token habis.
                    userPreferences.saveSession(
                        accessToken = accessToken,
                        refreshToken = authData.refreshtoken,
                        userId = authData.user?.id,
                        userName = authData.user?.name
                    )

                    _loginState.value = LoginState.Success(accessToken)
                    Log.d("LoginViewModel", "Login Success & Session Saved")
                } else {
                    _loginState.value = LoginState.Error(response.message)
                }
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Terjadi kesalahan koneksi"
                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }
}
