package com.example.baper_andoid.ui.screen.lihatpesanan

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.baper_andoid.ui.theme.InterFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LihatPesananScreen(
    viewModel: LihatPesananViewModel,
    initialTab: Int = 0,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val textColorPrimary = Color(0xFF0F172A)
    val textColorSecondary = Color(0xFF64748B)
    
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf("Belum Bayar", "Sudah Lunas")

    var showConfirmDialog by remember { mutableStateOf(false) }
    var orderToConfirm by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = viewModel.orders.filter { it.status == tabs[selectedTab] }
    val totalAmount = filteredOrders.sumOf { it.amount.replace("Rp ", "").replace(".", "").toInt() }

    Scaffold(
        containerColor = bgGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Lihat Pesanan", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color = Color.Black
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                        text = "Total: ${filteredOrders.size} pesanan ${tabs[selectedTab].lowercase()}",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    val interactionSource = remember { MutableInteractionSource() }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(color = Color.Gray)
                            ) { 
                                selectedTab = index 
                            }
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) brandGreen else textColorSecondary.copy(alpha = 0.6f),
                            fontFamily = InterFamily
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(if (isSelected) brandGreen else Color.Transparent)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredOrders) { order ->
                    OrderCard(
                        order = order, 
                        brandGreen = brandGreen, 
                        textColorPrimary = textColorPrimary, 
                        textColorSecondary = textColorSecondary,
                        onClick = { onNavigateToChat(order.chatId) },
                        onConfirmClick = {
                            orderToConfirm = order
                            showConfirmDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showConfirmDialog && orderToConfirm != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                val interactionSource = remember { MutableInteractionSource() }
                Text(
                    text = "Konfirmasi",
                    color = brandGreen,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFamily,
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = Color.Gray)
                        ) {
                            viewModel.confirmOrder(orderToConfirm!!.id)
                            showConfirmDialog = false
                            orderToConfirm = null
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            dismissButton = {
                val interactionSource = remember { MutableInteractionSource() }
                Text(
                    text = "Batal",
                    color = textColorSecondary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = InterFamily,
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = Color.Gray)
                        ) {
                            showConfirmDialog = false
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            title = { Text("Konfirmasi Pesanan", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("Apakah Anda yakin untuk konfirmasi pesanan ${orderToConfirm!!.id} ini?", color = Color.Black) },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun OrderCard(
    order: Order,
    brandGreen: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    onClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.Gray)
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary,
                    fontFamily = InterFamily
                )
                
                Surface(
                    color = if (order.status == "Belum Bayar") Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (order.status == "Belum Bayar") Color(0xFFD97706) else Color(0xFF107C42),
                        fontFamily = InterFamily
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OrderInfoRow(icon = Icons.Default.Person, text = order.customerName, textColor = textColorPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            OrderInfoRow(icon = Icons.Default.Inventory2, text = order.packageName, textColor = textColorSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OrderInfoRow(icon = Icons.Default.CalendarToday, text = order.date, textColor = textColorSecondary)
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL TAGIHAN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorSecondary,
                        fontFamily = InterFamily
                    )
                    Text(
                        text = order.amount,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = brandGreen,
                        fontFamily = InterFamily
                    )
                }
                
                if (order.status == "Belum Bayar") {
                    Button(
                        onClick = onConfirmClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brandGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Konfirmasi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderInfoRow(icon: ImageVector, text: String, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF107C42).copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontFamily = InterFamily
        )
    }
}
