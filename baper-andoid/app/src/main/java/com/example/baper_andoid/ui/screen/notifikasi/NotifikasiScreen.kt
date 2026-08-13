package com.example.baper_andoid.ui.screen.notifikasi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baper_andoid.ui.theme.InterFamily

data class NotifikasiItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifikasiScreen(
    onBack: () -> Unit
) {
    val bgGray = Color(0xFFF7F9F8)
    val textColorPrimary = Color(0xFF0F172A)
    val textColorSecondary = Color(0xFF64748B)

    val notifications = listOf(
        NotifikasiItem(
            1, "Pesanan Baru!", "Anda mendapatkan pesanan baru dari Andi Wijaya.", "2 menit yang lalu", 
            Icons.Default.ShoppingCart, Color(0xFF107C42)
        ),
        NotifikasiItem(
            2, "Update Sistem", "Versi terbaru BAPER v1.2 sudah tersedia.", "1 jam yang lalu", 
            Icons.Default.SystemUpdate, Color(0xFF3B82F6)
        ),
        NotifikasiItem(
            3, "Tagihan Menunggu", "Ada 5 pesanan yang belum dikonfirmasi pembayarannya.", "3 jam yang lalu", 
            Icons.Default.Notifications, Color(0xFFF59E0B)
        ),
        NotifikasiItem(
            4, "Tips Hari Ini", "Gunakan fitur rekap otomatis untuk menghemat waktu Anda.", "Kemarin", 
            Icons.Default.Notifications, Color(0xFF64748B)
        )
    )

    Scaffold(
        containerColor = bgGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Notifikasi", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color = Color.Black
                    ) 
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.Gray)
                            ) { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { item ->
                NotificationCard(item, textColorPrimary, textColorSecondary)
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotifikasiItem,
    textColorPrimary: Color,
    textColorSecondary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color = textColorPrimary
                    )
                    Text(
                        text = item.time,
                        fontSize = 11.sp,
                        fontFamily = InterFamily,
                        color = textColorSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.message,
                    fontSize = 13.sp,
                    fontFamily = InterFamily,
                    color = textColorSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
