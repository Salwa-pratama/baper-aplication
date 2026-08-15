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
fun BottomNavBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val brandGreen = Color(0xFF107C42)
    val inactiveColor = Color.Black // Tetap hitam sesuai permintaan sebelumnya
    
    // NavigationBar murni tanpa shadow/tonal agar tidak ada garis otomatis dari sistem
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            
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
                    if (selectedIndex != index) {
                        onItemSelected(index)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE8F5E9)
                )
            )
        }
    }
}
