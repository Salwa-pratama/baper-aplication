package com.example.baper_andoid.ui.screen.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baper_andoid.data.remote.RetrofitClient
import com.example.baper_andoid.data.repository.HomeRepository
import com.example.baper_andoid.data.repository.ProductRepository
import com.example.baper_andoid.navigation.BottomNavItem
import com.example.baper_andoid.ui.components.BottomNavBar
import com.example.baper_andoid.ui.screen.chat.BaperChat
import com.example.baper_andoid.ui.screen.chat.ChatViewModel
import com.example.baper_andoid.ui.screen.bot.BotViewModel
import com.example.baper_andoid.ui.screen.profil.ProfilViewModel
import com.example.baper_andoid.ui.screen.profil.ProfilScreen
import com.example.baper_andoid.ui.screen.rekap.RekapScreen
import com.example.baper_andoid.ui.screen.produk.ProdukScreen
import com.example.baper_andoid.ui.screen.produk.ProdukViewModel
import com.example.baper_andoid.ui.screen.produk.ProdukViewModelFactory
import com.example.baper_andoid.ui.theme.InterFamily

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel,
    botViewModel: BotViewModel,
    profilViewModel: ProfilViewModel,
    onLogout: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToBotStatus: () -> Unit,
    onNavigateToLihatPesanan: (Int) -> Unit,
    onNavigateToRekapDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val homeRepository = remember { HomeRepository(RetrofitClient.getInstance(context)) }
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(homeRepository))
    
    val productRepository = remember { ProductRepository(RetrofitClient.getInstance(context)) }
    val produkViewModel: ProdukViewModel = viewModel(factory = ProdukViewModelFactory(productRepository))
    
    val uiState by homeViewModel.uiState.collectAsState()

    val bottomNavController = rememberNavController()
    val bgGray = Color(0xFFF7F9F8)

    Scaffold(
        containerColor = bgGray,
        bottomBar = {
            BottomNavBar(navController = bottomNavController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Beranda.route
            ) {
                composable(BottomNavItem.Beranda.route) {
                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF107C42))
                        }
                    } else {
                        val name by profilViewModel.nama
                        val imageUri by profilViewModel.profileImageUri
                        
                        DashboardContent(
                            name = name,
                            imageUri = imageUri,
                            chatViewModel = chatViewModel,
                            botViewModel = botViewModel,
                            onNavigateToChat = onNavigateToChat,
                            onNavigateToBotStatus = onNavigateToBotStatus,
                            onNavigateToLihatPesanan = onNavigateToLihatPesanan
                        )
                    }
                }
                composable(BottomNavItem.Produk.route) {
                    ProdukScreen(viewModel = produkViewModel)
                }
                composable(BottomNavItem.Rekap.route) {
                    RekapScreen(
                        onNavigateToRekapDetail = onNavigateToRekapDetail
                    )
                }
                composable(BottomNavItem.Profil.route) {
                    ProfilScreen(
                        viewModel = profilViewModel,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

// --- Komponen Konten Dashboard (Beranda) ---

@Composable
fun DashboardContent(
    name: String,
    imageUri: android.net.Uri?,
    chatViewModel: ChatViewModel,
    botViewModel: BotViewModel,
    onNavigateToChat: (String) -> Unit,
    onNavigateToBotStatus: () -> Unit,
    onNavigateToLihatPesanan: (Int) -> Unit
) {
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val textColorPrimary = Color(0xFF0F172A)
    
    // Mengambil data chat langsung dari ViewModel
    val chats = chatViewModel.chatList
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGray)
    ) {
        // 1. Area Header yang Tetap (Fixed)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgGray)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            DashboardHeader(
                name = name,
                imageUri = imageUri,
                textColor = textColorPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFE2E8F0)
            )
        }

        // 2. Area Konten yang bisa digulir (Scrollable)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
        ) {
            item {
                SummaryCard(brandGreen)
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            item {
                Text(
                    text = "AKSI CEPAT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFamily,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                QuickActionsSection(
                    brandGreen = brandGreen,
                    textColor = textColorPrimary,
                    botViewModel = botViewModel,
                    onNavigateToBotStatus = onNavigateToBotStatus,
                    onNavigateToLihatPesanan = { onNavigateToLihatPesanan(0) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Obrolan Terbaru",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color = textColorPrimary
                    )
                    
                    // Efek Interaksi Teks (Tanpa background button)
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    
                    Text(
                        text = "Lihat Semua",
                        color = if (isPressed) brandGreen.copy(alpha = 0.6f) else brandGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFamily,
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = Color.Gray)
                        ) {
                            // Logic Lihat Semua
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Loop data chat dari ViewModel
            items(chats) { chat ->
                ChatListItem(
                    chat = chat, 
                    textColor = textColorPrimary, 
                    onClick = { onNavigateToChat(chat.id) }
                )
            }
        }
    }
}

@Composable
fun DashboardHeader(
    name: String,
    imageUri: android.net.Uri?,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "Halo,",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontFamily = InterFamily,
                    style = androidx.compose.ui.text.TextStyle(
                        platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    fontFamily = InterFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = androidx.compose.ui.text.TextStyle(
                        platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }
        }
        
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SummaryCard(brandGreen: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = brandGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Rekapan Bulan Ini",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontFamily = InterFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rp 12.450.000",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFamily
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryMiniInfo(
                    label = "Lunas",
                    value = "85",
                    modifier = Modifier.weight(1f)
                )
                SummaryMiniInfo(
                    label = "Pending",
                    value = "12",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SummaryMiniInfo(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickActionsSection(
    brandGreen: Color, 
    textColor: Color, 
    botViewModel: BotViewModel,
    onNavigateToBotStatus: () -> Unit,
    onNavigateToLihatPesanan: (Int) -> Unit
) {
    val isBotActive by botViewModel.isBotActive
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionButton(
            title = "Lihat Pesanan",
            icon = Icons.Default.Add,
            brandColor = brandGreen,
            textColor = textColor,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToLihatPesanan(0) }
        )
        QuickActionButton(
            title = "Cek Status Bot",
            icon = Icons.Default.Share,
            brandColor = Color(0xFFF59E0B),
            textColor = textColor,
            showStatusIndicator = true,
            isStatusActive = isBotActive,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToBotStatus
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    brandColor: Color,
    textColor: Color,
    showStatusIndicator: Boolean = false,
    isStatusActive: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val brandGreen = Color(0xFF107C42)
    val interactionSource = remember { MutableInteractionSource() }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = Color.Gray)
                ) { onClick() }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brandColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = brandColor, modifier = Modifier.size(20.dp))
                    }
                    
                    // Status Indicator (Lampu Menyala)
                    if (showStatusIndicator) {
                        val statusColor = if (isStatusActive) Color(0xFF107C42) else Color(0xFFDC3545)
                        
                        // Efek Animasi Denyut (Pulse)
                        val infiniteTransition = rememberInfiniteTransition(label = "glowTransition")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 0.7f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "glowAlpha"
                        )
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.6f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "glowScale"
                        )

                        Box(
                            modifier = Modifier.padding(top = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Layer 1: Cahaya Luar (Glow)
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .graphicsLayer(scaleX = scale, scaleY = scale)
                                    .clip(CircleShape)
                                    .background(statusColor.copy(alpha = alpha))
                            )
                            // Layer 2: Inti Lampu (Solid)
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
            }
        }
    }
}

@Composable
fun ChatListItem(chat: BaperChat, textColor: Color, onClick: () -> Unit) {
    val brandGreen = Color(0xFF107C42)
    val interactionSource = remember { MutableInteractionSource() }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEF2F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = Color.Gray)
                ) { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = chat.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = InterFamily,
                        color = textColor,
                        style = androidx.compose.ui.text.TextStyle(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                    Text(
                        text = chat.lastMessage,
                        fontSize = 12.sp,
                        fontFamily = InterFamily,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = androidx.compose.ui.text.TextStyle(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
                
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = chat.time,
                        fontSize = 11.sp,
                        fontFamily = InterFamily,
                        color = Color(0xFF94A3B8)
                    )
                    
                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(brandGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = InterFamily,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = androidx.compose.ui.text.TextStyle(
                                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                    lineHeightStyle = androidx.compose.ui.text.style.LineHeightStyle(
                                        alignment = androidx.compose.ui.text.style.LineHeightStyle.Alignment.Center,
                                        trim = androidx.compose.ui.text.style.LineHeightStyle.Trim.Both
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderPage(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}
