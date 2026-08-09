package com.example.baper_andoid.ui.screen.onboarding

import androidx.compose.foundation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baper_andoid.R
import com.example.baper_andoid.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun DashboardPreview(modifier: Modifier = Modifier) {
    val brandGreen = Color(0xFF107C42)
    val lightGreen = Color(0xFFE8F5E9)
    val cardBg = Color.White

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card Total Pendapatan
            Card(
                modifier = Modifier.weight(1.5f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Pendapatan", fontSize = 12.sp, color = Color.Gray)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(lightGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = brandGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        "Rp 4.200.000",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = brandGreen,
                        trackColor = lightGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "75% dari target bulan ini",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            // Right column small cards
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallStatCard(
                    icon = Icons.Default.Inventory,
                    count = "120",
                    label = "Order Selesai",
                    brandGreen = brandGreen,
                    lightGreen = lightGreen
                )
                SmallStatCard(
                    icon = Icons.Default.ChatBubbleOutline,
                    count = "42",
                    label = "Pesan Baru",
                    brandGreen = brandGreen,
                    lightGreen = lightGreen
                )
            }
        }

        // Card Rekap Terakhir
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rekap Terakhir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                RekapItem(name = "Andi", amount = "+Rp 100k", letter = "A")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                RekapItem(name = "Budi", amount = "+Rp 50k", letter = "B")
            }
        }
    }
}

@Composable
fun SmallStatCard(
    icon: ImageVector,
    count: String,
    label: String,
    brandGreen: Color,
    lightGreen: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(lightGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = brandGreen, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(label, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun RekapItem(name: String, amount: String, letter: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFBBDEFB)),
                contentAlignment = Alignment.Center
            ) {
                Text(letter, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Column {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Berhasil direkap", fontSize = 12.sp, color = Color.Gray)
            }
        }
        Text(amount, color = Color(0xFF107C42), fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

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

@Composable
fun BaperOnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = listOf(
        BaperPageData(
            title = "Selamat Datang di BAPER",
            description = "Solusi cerdas untuk bantu pesan dan rekap transaksi Anda lebih mudah."
        ),
        BaperPageData(
            title = "Rekap Otomatis",
            description = "Hemat waktu dengan rekap daya otomatis. Semua pesanan tercatat rapi tanpa perlu input manual."
        ),
        BaperPageData(
            title = "Siap untuk Mulai?",
            description = "Bergabunglah dengan ribuan pengguna yang telah mempermudah bisnis mereka dengan BAPER"
        )
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    
    // Warna Profesional
    val brandGreen = Color(0xFF107C42)
    val backgroundColor = Color(0xFFF7F9F8)
    val textColorPrimary = Color(0xFF0F172A)
    val textColorSecondary = Color(0xFF64748B)
    
    val scope = rememberCoroutineScope()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        // Efek Cahaya Hijau Lembut (Blob) di Background - Centered better
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(brandGreen.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(500f, 400f),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState, 
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 24.dp)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Content Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (page == 0) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_utama),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
                            )
                        } else if (page == 1) {
                            DashboardPreview(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            )
                        } else if (page == 2) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_utama),
                                contentDescription = null,
                                modifier = Modifier.size(160.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = pages[page].title,
                        style = MaterialTheme.typography.displayMedium,
                        color = textColorPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = pages[page].description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColorSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Bagian Bawah: Tombol & Indikator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Tombol Navigasi
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (pagerState.currentPage == pages.size - 1) {
                        RoundedSlideButton(
                            onClick = onFinished,
                            text = "Buat Akun",
                            containerColor = brandGreen
                        )
                        OutlinedButton(
                            onClick = onFinished,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = brandGreen)
                        ) {
                            Text(
                                "Sudah punya akun? Masuk",
                                style = MaterialTheme.typography.labelLarge,
                                color = brandGreen
                            )
                        }
                    } else {
                        RoundedSlideButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            text = "Lanjut",
                            containerColor = brandGreen,
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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

                // Indikator Halaman (Dots)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { i ->
                        val isSelected = pagerState.currentPage == i
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            label = "dotWidth"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(height = 6.dp, width = width)
                                .clip(CircleShape)
                                .background(if (isSelected) brandGreen else Color(0xFFD1D5DB))
                        )
                    }
                }
            }
        }
    }
}

data class BaperPageData(val title: String, val description: String)
