package com.example.baper_andoid.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baper_andoid.navigation.BottomNavItem
import com.example.baper_andoid.ui.components.BottomNavBar

@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
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
                    DashboardPage()
                }
                composable(BottomNavItem.Produk.route) {
                    PlaceholderPage(title = "Halaman Produk")
                }
                composable(BottomNavItem.Rekap.route) {
                    PlaceholderPage(title = "Halaman Rekap")
                }
                composable(BottomNavItem.Profil.route) {
                    ProfilePage(onLogout = onLogout)
                }
            }
        }
    }
}

@Composable
fun PlaceholderPage(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ProfilePage(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Halaman Profil", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
        ) {
            Text("Keluar (Logout)", color = Color.White)
        }
    }
}
