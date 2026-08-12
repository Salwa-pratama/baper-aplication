package com.example.baper_andoid.ui.screen.bot

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baper_andoid.ui.theme.InterFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotStatusScreen(
    viewModel: BotViewModel,
    onBack: () -> Unit
) {
    val isBotActive by viewModel.isBotActive
    val botPrompt by viewModel.botPrompt
    val apiKey by viewModel.apiKey
    
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val gearOrange = Color(0xFFF59E0B)
    val textColor = Color.Black

    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = brandGreen,
        backgroundColor = brandGreen.copy(alpha = 0.2f)
    )

    val customTextStyle = TextStyle(
        fontSize = 15.sp,
        fontFamily = InterFamily,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        color = textColor,
        lineHeight = 20.sp
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            containerColor = bgGray,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Cek Status Bot", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFamily,
                            color = textColor
                        ) 
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Kembali",
                                modifier = Modifier.size(20.dp),
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = bgGray)
                )
            }
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    scope.launch {
                        delay(2000)
                        isRefreshing = false
                    }
                },
                modifier = Modifier.padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Status Bot", 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 16.sp,
                                        fontFamily = InterFamily,
                                        color = textColor
                                    )
                                    Text(
                                        "Aktifkan atau nonaktifkan bot", 
                                        fontSize = 12.sp, 
                                        color = textColor,
                                        fontFamily = InterFamily
                                    )
                                }
                                Switch(
                                    checked = isBotActive,
                                    onCheckedChange = { viewModel.toggleBot() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = brandGreen,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.LightGray,
                                        uncheckedBorderColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(gearOrange.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Settings, 
                                            contentDescription = null, 
                                            tint = gearOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Pengaturan Karakteristik Bot", 
                                            fontWeight = FontWeight.Bold, 
                                            fontSize = 15.sp,
                                            fontFamily = InterFamily,
                                            color = textColor
                                        )
                                        Text(
                                            "Masukkan prompt untuk karakteristik Bot", 
                                            fontSize = 12.sp, 
                                            color = textColor,
                                            fontFamily = InterFamily
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val interactionSource = remember { MutableInteractionSource() }
                                BasicTextField(
                                    value = botPrompt,
                                    onValueChange = { viewModel.onPromptChange(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    textStyle = customTextStyle,
                                    cursorBrush = SolidColor(brandGreen),
                                    interactionSource = interactionSource,
                                    decorationBox = { innerTextField ->
                                        OutlinedTextFieldDefaults.DecorationBox(
                                            value = botPrompt,
                                            innerTextField = {
                                                Box(contentAlignment = Alignment.TopStart) {
                                                    if (botPrompt.isEmpty()) {
                                                        Text(
                                                            text = "Contoh: Kamu adalah asisten toko online yang ramah dan profesional. Jawab pertanyaan pelanggan dengan sopan, bantu proses pesanan, dan berikan rekomendasi produk yang sesuai...",
                                                            style = customTextStyle.copy(color = Color.LightGray)
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            },
                                            enabled = true,
                                            singleLine = false,
                                            visualTransformation = VisualTransformation.None,
                                            interactionSource = interactionSource,
                                            container = {
                                                OutlinedTextFieldDefaults.Container(
                                                    enabled = true,
                                                    isError = false,
                                                    interactionSource = interactionSource,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFFE2E8F0),
                                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                            },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    "API Key", 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 15.sp,
                                    fontFamily = InterFamily,
                                    color = textColor
                                )
                                Text(
                                    "Gunakan API key Anda sendiri (opsional)", 
                                    fontSize = 12.sp, 
                                    color = textColor,
                                    fontFamily = InterFamily
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val interactionSource = remember { MutableInteractionSource() }
                                BasicTextField(
                                    value = apiKey,
                                    onValueChange = { viewModel.onApiKeyChange(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = customTextStyle,
                                    cursorBrush = SolidColor(brandGreen),
                                    interactionSource = interactionSource,
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        OutlinedTextFieldDefaults.DecorationBox(
                                            value = apiKey,
                                            innerTextField = {
                                                Box(contentAlignment = Alignment.TopStart) {
                                                    if (apiKey.isEmpty()) {
                                                        Text(
                                                            text = "••••••••••••sk-xxxx",
                                                            style = customTextStyle.copy(color = Color.LightGray)
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            },
                                            enabled = true,
                                            singleLine = true,
                                            visualTransformation = VisualTransformation.None,
                                            interactionSource = interactionSource,
                                            leadingIcon = {
                                                Icon(Icons.Default.Key, contentDescription = null, tint = Color.LightGray)
                                            },
                                            container = {
                                                OutlinedTextFieldDefaults.Container(
                                                    enabled = true,
                                                    isError = false,
                                                    interactionSource = interactionSource,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedContainerColor = bgGray.copy(alpha = 0.5f),
                                                        unfocusedContainerColor = bgGray.copy(alpha = 0.5f),
                                                        focusedBorderColor = Color(0xFFE2E8F0),
                                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                            },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                                        )
                                    }
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { viewModel.saveApiKey() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                                ) {
                                    Text(
                                        "Simpan API Key", 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
