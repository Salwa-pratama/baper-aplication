package com.example.baper_andoid.ui.screen.rekap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baper_andoid.ui.screen.lihatpesanan.Order
import com.example.baper_andoid.ui.screen.lihatpesanan.OrderCard
import com.example.baper_andoid.ui.theme.InterFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekapDetailScreen(
    month: String,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val textColorPrimary = Color(0xFF0F172A)
    val textColorSecondary = Color(0xFF64748B)

    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    // Mock data for the specific month (only paid orders)
    val paidOrders = listOf(
        Order("#ORD-2026-004", "Dewi Sartika", "Paket Hemat x1", "3 $month 2026", "Rp 75.000", "Sudah Lunas", "4"),
        Order("#ORD-2026-005", "Rian Hidayat", "Paket Premium x2", "2 $month 2026", "Rp 440.000", "Sudah Lunas", "5"),
        Order("#ORD-2026-008", "Indra Wijaya", "Paket Premium x1", "1 $month 2026", "Rp 220.000", "Sudah Lunas", "8")
    )

    val totalAmount = paidOrders.sumOf { it.amount.replace("Rp ", "").replace(".", "").toInt() }

    Scaffold(
        containerColor = bgGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Rekap Pesanan $month", 
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
                            .clip(androidx.compose.foundation.shape.CircleShape)
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
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    Text(
                        text = "Total: ${paidOrders.size} pesanan sudah lunas",
                        fontSize = 11.sp,
                        color = textColorSecondary,
                        fontFamily = InterFamily
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Rp ${String.format(Locale("in", "ID"), "%,d", totalAmount).replace(",", ".")}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = brandGreen,
                        fontFamily = InterFamily
                    )
                }
            }
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
                    .fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(paidOrders) { order ->
                    OrderCard(
                        order = order, 
                        brandGreen = brandGreen, 
                        textColorPrimary = textColorPrimary, 
                        textColorSecondary = textColorSecondary,
                        onClick = { onNavigateToChat(order.chatId) },
                        onConfirmClick = {} // No confirm button on this screen
                    )
                }
            }
        }
    }
}
