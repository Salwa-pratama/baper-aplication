package com.example.baper_andoid.ui.screen.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var firstname by remember {mutableStateOf("")}
    var lastname by remember {mutableStateOf("")}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Daftar Akun Baru", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = firstname, onValueChange = { firstname = it }, label = { Text("Nama Lengkap") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = lastname, onValueChange = { lastname = it }, label = { Text("Nama Lengkap") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(Modifier.height(24.dp))
        
        Button(
            onClick = { onRegisterSuccess() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Daftar")
        }
        
        TextButton(onClick = onBackToLogin) {
            Text("Sudah punya akun? Login")
        }
    }
}
