package com.example.baper_andoid.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Warna tema WhatsApp
private val WaGreenDark = Color(0xFF128C7E)
private val WaGreen = Color(0xFF25D366)
private val WaGreenLight = Color(0xFFDCF8C6)
private val WaBackground = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val dummyStores = listOf(
        "Barber Jaya" to "Antrian: 3",
        "Cukur Kece" to "Antrian: 0",
        "Modern Barbershop" to "Tutup"
    )

    Scaffold(
        containerColor = WaBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text("Beranda", fontWeight = FontWeight.Bold, color = Color.White)
                },
                actions = {
                    IconButton(onClick = { /* TODO: notifikasi */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WaGreenDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header sapaan
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WaGreen)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Halo, Ananda 👋",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Mau cukur di mana hari ini?",
                    color = WaGreenLight,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Search bar sederhana (visual only dulu)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text("Cari toko barbershop...", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Toko Terdekat",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(8.dp))

            // List toko (dummy dulu, nanti diganti data dari ViewModel/API)
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(dummyStores) { (name, status) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WaGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = WaGreenDark)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(status, color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}