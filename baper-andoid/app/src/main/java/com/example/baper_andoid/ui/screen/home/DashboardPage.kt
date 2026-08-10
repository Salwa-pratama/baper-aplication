package com.example.baper_andoid.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.baper_andoid.ui.theme.InterFamily

@Preview(showBackground = true)
@Composable
fun DashboardPagePreview() {
    DashboardPage()
}

@Composable
fun DashboardPage() {
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val textColorPrimary = Color(0xFF0F172A)
    
    // Optimasi: Gunakan remember agar list data tidak di-instantiate ulang setiap recomposition
    val mockChats = remember { getMockChats() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGray)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
    ) {
        // ... (Header, Summary, Quick Actions tetap sama)
        item {
            DashboardHeader(textColorPrimary)
        }
        
        item {
            SummaryCard(brandGreen)
        }
        
        item {
            QuickActionsSection(brandGreen, textColorPrimary)
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                TextButton(onClick = {}) {
                    Text("Lihat Semua", color = brandGreen, fontSize = 14.sp)
                }
            }
        }
        
        // Optimasi: Menggunakan list yang sudah di-remember
        items(mockChats) { chat ->
            ChatListItem(chat, textColorPrimary)
        }
    }
}

@Composable
fun DashboardHeader(textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Halo, Toko Berkah",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = InterFamily
                )
                Text(
                    text = "Selamat Pagi! \uD83D\uDC4B",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontFamily = InterFamily
                )
            }
        }
        
        IconButton(
            onClick = {},
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = textColor)
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
fun QuickActionsSection(brandGreen: Color, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionButton(
            title = "Pesanan",
            icon = Icons.Default.ShoppingCart,
            brandColor = brandGreen,
            textColor = textColor,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            title = "Status Bot",
            icon = Icons.Default.SmartToy,
            brandColor = Color(0xFF3B82F6),
            textColor = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    brandColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
        }
    }
}

@Composable
fun ChatListItem(chat: BaperChat, textColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chat.name.take(1),
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = textColor
                )
                Text(
                    text = chat.lastMessage,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = chat.time, fontSize = 11.sp, color = Color.Gray)
                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF107C42)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

data class BaperChat(
    val name: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0
)

fun getMockChats() = listOf(
    BaperChat("Andi Saputra", "Halo, pesanan saya sudah dikirim?", "10:20", 2),
    BaperChat("Budi Setiawan", "Terima kasih infonya.", "09:45"),
    BaperChat("Citra Lestari", "Bisa kirim foto produknya?", "Kemarin", 1),
    BaperChat("Dedi Kurniawan", "Siap gan, langsung order.", "Kemarin")
)
