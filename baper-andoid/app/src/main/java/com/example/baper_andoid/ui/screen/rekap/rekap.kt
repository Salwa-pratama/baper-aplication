package com.example.baper_andoid.ui.screen.rekap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.TableView
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
import com.example.baper_andoid.ui.theme.InterFamily

data class RekapData(
    val id: String,
    val month: String,
    val description: String,
    val amount: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekapScreen(
    viewModel: RekapViewModel,
    onNavigateToRekapDetail: (String) -> Unit
) {
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val textColorSecondary = Color(0xFF64748B)

    var selectedYear by remember { mutableStateOf("2026") }
    val years = viewModel.availableYears
    
    val rekapDataMap by viewModel.rekapDataMap.collectAsState()
    val reports = rekapDataMap[selectedYear] ?: emptyList()
    val isRefreshing by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGray)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Rekap Bulanan",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = InterFamily,
            color = Color.Black
        )
        Text(
            text = selectedYear,
            fontSize = 13.sp,
            color = textColorSecondary,
            fontFamily = InterFamily
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Year Selection Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(years) { year ->
                val isSelected = year == selectedYear
                val interactionSource = remember { MutableInteractionSource() }
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedYear = year },
                    label = { 
                        Text(
                            year, 
                            fontFamily = InterFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ) 
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = brandGreen,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = textColorSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0xFFE2EBE5),
                        selectedBorderColor = brandGreen
                    ),
                    interactionSource = interactionSource
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchOrders() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reports) { report ->
                    RekapCard(
                        report = report, 
                        brandGreen = brandGreen, 
                        textColorSecondary = textColorSecondary,
                        onClick = { onNavigateToRekapDetail("${report.month}-$selectedYear") }
                    )
                }
            }
        }
    }
}

@Composable
fun RekapCard(
    report: RekapData,
    brandGreen: Color,
    textColorSecondary: Color,
    onClick: () -> Unit
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = report.id,
                fontSize = 10.sp,
                color = textColorSecondary.copy(alpha = 0.6f),
                fontFamily = InterFamily,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brandGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description, 
                        contentDescription = null, 
                        tint = brandGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Rekap Bulan ${report.month}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color = Color.Black
                    )
                    Text(
                        text = report.description,
                        fontSize = 12.sp,
                        color = textColorSecondary,
                        fontFamily = InterFamily
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL PENDAPATAN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorSecondary,
                        fontFamily = InterFamily,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = report.amount,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = brandGreen,
                        fontFamily = InterFamily
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.Gray)
                        ) { /* Export Action */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TableView, 
                        contentDescription = "Unduh Excel",
                        tint = brandGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
