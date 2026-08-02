package com.example.baper_andoid.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baper_andoid.data.remote.RetrofitClient
import com.example.baper_andoid.data.repository.AuthRepository
import androidx.compose.ui.unit.dp
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val authRepository = remember { AuthRepository(RetrofitClient.instance) }
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository))
    val state by viewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            onLoginSuccess() // panggil navigasi ke Home, BUKAN Intent lagi
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(Modifier.height(16.dp))

        Button(onClick = { viewModel.login(email, password) }, enabled = state !is LoginState.Loading) {
            Text(if (state is LoginState.Loading) "Loading..." else "Login")
        }

        if (state is LoginState.Error) {
            Text((state as LoginState.Error).message, color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Belum punya akun? Daftar")
        }
    }
}