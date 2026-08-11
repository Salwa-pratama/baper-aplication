package com.example.baper_andoid.ui.screen.register

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
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
import com.example.baper_andoid.data.remote.RetrofitClient
import com.example.baper_andoid.data.repository.AuthRepository

@Composable
fun RoundedSlideButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    containerColor: Color = Color(0xFF107C42),
    slideColor: Color = Color(0xFF0D6335),
    contentColor: Color = Color.White,
    radius: Int = 16,
    isLoading: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(slideProgress)
                    .background(slideColor)
            )
            
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

enum class RegisterStep {
    PERSONAL_DATA,
    BUSINESS_DATA
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val authRepository = remember { AuthRepository(RetrofitClient.instance) }
    val regViewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(authRepository))
    val state by regViewModel.registerState.collectAsState()

    var currentStep by remember { mutableStateOf(RegisterStep.PERSONAL_DATA) }

    // Step 1: Data Diri
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Step 2: Data Bisnis
    var businessName by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var businessWhatsapp by remember { mutableStateOf("") }

    // Warna Profesional
    val brandGreen = Color(0xFF107C42)
    val backgroundColor = Color(0xFFF7F9F8)
    val textColorSecondary = Color(0xFF64748B)
    val textColorPrimary = Color(0xFF0F172A)

    val customTextSelectionColors = TextSelectionColors(
        handleColor = brandGreen,
        backgroundColor = brandGreen.copy(alpha = 0.4f)
    )

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
        if (state is RegisterState.Success) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_logo_utama),
                contentDescription = null,
                modifier = Modifier.size(140.dp, 100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (currentStep == RegisterStep.PERSONAL_DATA) "Daftar Akun Baru" else "Informasi Bisnis",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColorPrimary,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (currentStep == RegisterStep.PERSONAL_DATA) 
                    "Lengkapi data diri Anda untuk bergabung dengan komunitas kami." 
                else 
                    "Beritahu kami sedikit tentang bisnis Anda untuk penyesuaian layanan.",
                fontSize = 15.sp,
                color = textColorSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut())
                            } else {
                                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width } + fadeOut())
                            }.using(
                                SizeTransform(clip = false)
                            )
                        },
                        label = "formTransition"
                    ) { step ->
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            if (step == RegisterStep.PERSONAL_DATA) {
                                Text(
                                    text = "Nama Lengkap",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Masukkan nama lengkap", color = Color(0xFF94A3B8)) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = brandGreen) },
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Email",
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
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

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
                                    placeholder = { Text("Masukkan password", color = Color(0xFF94A3B8)) },
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
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Konfirmasi Password",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Ulangi password", color = Color(0xFF94A3B8)) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = brandGreen) },
                                    trailingIcon = {
                                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color(0xFF94A3B8)
                                            )
                                        }
                                    },
                                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                RoundedSlideButton(
                                    onClick = { currentStep = RegisterStep.BUSINESS_DATA },
                                    text = "Selanjutnya",
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
                            } else {
                                Text(
                                    text = "Nama Bisnis",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = businessName,
                                    onValueChange = { businessName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Masukkan nama bisnis Anda", color = Color(0xFF94A3B8)) },
                                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = brandGreen) },
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Jenis Bisnis",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = businessType,
                                    onValueChange = { businessType = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Contoh: Kuliner, Retail, Jasa", color = Color(0xFF94A3B8)) },
                                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = brandGreen) },
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Alamat Bisnis",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = businessAddress,
                                    onValueChange = { businessAddress = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Lokasi fisik bisnis Anda", color = Color(0xFF94A3B8)) },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = brandGreen) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "WhatsApp Bisnis",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = businessWhatsapp,
                                    onValueChange = { businessWhatsapp = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("812 3456 78xx", color = Color(0xFF94A3B8)) },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = brandGreen) },
                                    prefix = { Text("+62 ", color = textColorPrimary, fontWeight = FontWeight.Medium) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandGreen,
                                        unfocusedBorderColor = brandGreen.copy(alpha = 0.5f),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = textColorPrimary,
                                        unfocusedTextColor = textColorPrimary,
                                        cursorColor = brandGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(36.dp))

                                AnimatedVisibility(
                                    visible = state is RegisterState.Error,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Text(
                                        text = (state as? RegisterState.Error)?.message ?: "",
                                        color = Color(0xFFB91C1C),
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                RoundedSlideButton(
                                    onClick = {
                                        regViewModel.register(
                                            fullName = fullName,
                                            email = email,
                                            password = password,
                                            businessName = businessName,
                                            businessType = businessType,
                                            businessAddress = businessAddress,
                                            businessPhone = businessWhatsapp
                                        )
                                    },
                                    text = "Daftar Sekarang",
                                    isLoading = state is RegisterState.Loading,
                                    containerColor = brandGreen
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 32.dp)
                    .clickable { onBackToLogin() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun? ", 
                    color = textColorSecondary, 
                    fontSize = 14.sp
                )
                Text(
                    text = "Masuk",
                    color = brandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
