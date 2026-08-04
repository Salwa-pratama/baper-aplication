package com.example.baper_andoid.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baper_andoid.data.remote.RetrofitClient
import com.example.baper_andoid.data.repository.HomeRepository

private val BrandGreen = Color(0xFF28A745)
private val BrandGreenDark = Color(0xFF1E7E34)
private val BrandGreenLight = Color(0xFFD4EDDA)
private val BgGray = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val repository = remember { HomeRepository(RetrofitClient.instance) }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BgGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("BAPER Dashboard", fontWeight = FontWeight.ExtraBold, color = Color.White)
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandGreen)
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Halo, Pengusaha Sukses! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Berikut ringkasan bisnis Anda hari ini.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Omzet Hari Ini",
                            value = uiState.stats["omzet"] ?: "Rp 0",
                            icon = Icons.Default.MonetizationOn,
                            color = BrandGreen
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Pesanan Baru",
                            value = uiState.stats["pesanan"] ?: "0",
                            icon = Icons.Default.ShoppingCart,
                            color = Color(0xFF007BFF)
                        )
                    }
                }

                item {
                    Text(
                        text = "Transaksi Terbaru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                items(uiState.transactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        }
    }
}

@Composable
fun TransactionItem(transaction: com.example.baper_andoid.data.repository.Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BrandGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = BrandGreenDark)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("ID: ${transaction.id}", fontSize = 12.sp, color = Color.Gray)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(transaction.amount, fontWeight = FontWeight.Bold, color = BrandGreen)
            Text(
                text = transaction.status,
                fontSize = 11.sp,
                color = if (transaction.status == "Sukses") BrandGreen else Color.Red,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
