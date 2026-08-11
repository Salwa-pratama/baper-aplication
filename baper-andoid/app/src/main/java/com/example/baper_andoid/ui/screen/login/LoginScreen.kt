package com.example.baper_andoid.ui.screen.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baper_andoid.R
import com.example.baper_andoid.data.local.UserPreferences
import com.example.baper_andoid.data.remote.RetrofitClient
import com.example.baper_andoid.data.repository.AuthRepository

@Composable
fun RoundedSlideButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    containerColor: Color = Color(0xFF107C42),
    slideColor: Color = Color(0xFF0D6335), // Warna hijau yang lebih gelap untuk efek slide
    contentColor: Color = Color.White,
    radius: Int = 16,
    isLoading: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animasi untuk lebar background slide
    val slideProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "slideProgress"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(radius.dp),
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource,
        shadowElevation = if (isPressed) 0.dp else 2.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Layer Background Slide
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(slideProgress)
                    .background(slideColor)
            )
            
            // Konten Tombol (Teks & Ikon)
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                Text(
                    text = if (isLoading) "Memproses..." else text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                if (!isLoading && icon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    icon()
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val authRepository = remember { AuthRepository(RetrofitClient.instance) }
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository, userPreferences))
    val state by viewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Warna Profesional
    val brandGreen = Color(0xFF107C42)
    val backgroundColor = Color(0xFFF7F9F8)
    val textColorSecondary = Color(0xFF64748B) // Slate 500
    val textColorPrimary = Color(0xFF0F172A)   // Slate 900

    // Custom Selection Colors (Handle & Background)
    val customTextSelectionColors = TextSelectionColors(
        handleColor = brandGreen,
        backgroundColor = brandGreen.copy(alpha = 0.4f)
    )

    // Animasi Panah Bergerak (Maju Mundur)
    val infiniteTransition = rememberInfiniteTransition(label = "arrowTransition")
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowOffset"
    )

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding() // Tambahkan ini untuk edge-to-edge
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Tambahkan ini agar form naik saat keyboard muncul
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp)) // Dikurangi dari 80.dp

            // Logo Utama dengan ukuran yang lebih proporsional
            Image(
                painter = painterResource(id = R.drawable.ic_logo_utama),
                contentDescription = null,
                modifier = Modifier.size(160.dp, 120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp)) // Dikurangi dari 48.dp

            // Header Teks dengan Tipografi Kuat
            Text(
                text = "Selamat Datang!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColorPrimary,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(8.dp)) // Dikurangi dari 12.dp
            
            Text(
                text = "Masuk dengan email Anda untuk melanjutkan pengalaman terbaik.",
                fontSize = 15.sp,
                color = textColorSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp)) // Dikurangi dari 48.dp

            // Kartu Input Minimalis & Bersih
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat design
            ) {
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        // Input Email
                        val emailInteractionSource = remember { MutableInteractionSource() }

                        Text(
                            text = "Alamat Email",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorPrimary,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("contoh@email.com", color = Color(0xFF94A3B8)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = brandGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            interactionSource = emailInteractionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen,
                                unfocusedBorderColor = brandGreen.copy(alpha = 0.5f), // Border hijau transparan saat tidak fokus
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = textColorPrimary,
                                unfocusedTextColor = textColorPrimary,
                                cursorColor = brandGreen
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Input Password
                        val passwordInteractionSource = remember { MutableInteractionSource() }

                        Text(
                            text = "Password",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorPrimary,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Masukkan Password", color = Color(0xFF94A3B8)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = brandGreen) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            interactionSource = passwordInteractionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = brandGreen,
                                unfocusedBorderColor = brandGreen.copy(alpha = 0.5f), // Border hijau transparan saat tidak fokus
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = textColorPrimary,
                                unfocusedTextColor = textColorPrimary,
                                cursorColor = brandGreen
                            )
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                    // Pesan Error Minimalis
                    AnimatedVisibility(
                        visible = state is LoginState.Error,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFB91C1C),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = (state as? LoginState.Error)?.message ?: "",
                                color = Color(0xFFB91C1C),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                        // Tombol Login dengan Efek Rounded Slide
                        RoundedSlideButton(
                            onClick = { viewModel.login(email, password) },
                            text = "Masuk Sekarang",
                            isLoading = state is LoginState.Loading,
                            containerColor = brandGreen,
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward, 
                                    contentDescription = null, 
                                    modifier = Modifier
                                        .size(20.dp)
                                        .offset(x = arrowOffset.dp),
                                    tint = Color.White
                                )
                            }
                        )
                    }
                }
            }

            // Footer Daftar dengan Gaya Link Profesional
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 32.dp)
                    .clickable { onNavigateToRegister() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum punya akun? ", 
                    color = textColorSecondary, 
                    fontSize = 14.sp
                )
                Text(
                    text = "Daftar Gratis",
                    color = brandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
