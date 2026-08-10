package com.example.baper_andoid.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.baper_andoid.navigation.bottomNavItems
import com.example.baper_andoid.ui.theme.InterFamily

@Composable
fun BottomNavBar(navController: NavController) {
    val brandGreen = Color(0xFF107C42)
    val inactiveColor = Color.Black // Tetap hitam sesuai permintaan sebelumnya
    
    // Optimasi: Gunakan tonalElevation yang lebih rendah agar render bayangan tidak berat
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 2.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        
        // Optimasi: derivedStateOf agar tidak memicu recomposition berlebihan
        val currentRoute by remember(navBackStackEntry) {
            derivedStateOf { navBackStackEntry?.destination?.route }
        }

        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) brandGreen else inactiveColor
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontFamily = InterFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) brandGreen else inactiveColor
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Menghindari penumpukan stack navigasi
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE8F5E9)
                )
            )
        }
    }
}
