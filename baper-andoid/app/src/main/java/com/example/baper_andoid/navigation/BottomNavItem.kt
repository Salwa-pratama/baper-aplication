package com.example.baper_andoid.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Beranda : BottomNavItem("Beranda", Icons.Default.Home, "beranda")
    object Produk : BottomNavItem("Produk", Icons.Default.Inventory2, "produk")
    object Rekap : BottomNavItem("Rekap", Icons.Default.BarChart, "rekap")
    object Profil : BottomNavItem("Profil", Icons.Default.Person, "profil")
}

val bottomNavItems = listOf(
    BottomNavItem.Beranda,
    BottomNavItem.Produk,
    BottomNavItem.Rekap,
    BottomNavItem.Profil
)
