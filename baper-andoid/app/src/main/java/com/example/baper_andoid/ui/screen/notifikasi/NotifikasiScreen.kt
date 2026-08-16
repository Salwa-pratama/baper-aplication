package com.example.baper_andoid.ui.screen.notifikasi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import com.example.baper_andoid.ui.theme.InterFamily

data class NotifikasiItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val icon: ImageVector,
    val isUnread: Boolean = false,
    val type: NotifType,
    val actionId: String = ""
)

enum class NotifType {
    PESANAN_BARU,
    PEMBAYARAN_DITERIMA,
    PESAN_BARU,
    STOK_PENGINGAT
}

@Composable
fun NotifikasiPanelContent(
    notifications: List<NotifikasiItem>,
    onMarkAllRead: () -> Unit = {},
    onSwipeUp: () -> Unit = {},
    onNotificationClick: (NotifikasiItem) -> Unit
) {
    val brandGreen = Color(0xFF107C42)
    val textColorPrimary = Color(0xFF1E2924)
    val textColorSecondary = Color(0xFF5A6E65)
    val dividerColor = Color(0xFFE2EBE5)
    
    val swipeModifier = Modifier.pointerInput(Unit) {
        detectVerticalDragGestures { change, dragAmount ->
            if (dragAmount < -12f) {
                onSwipeUp()
            }
            change.consume()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(swipeModifier)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notifikasi",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = InterFamily,
                color = textColorPrimary
            )
            
            Text(
                text = "Tandai semua dibaca",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFamily,
                color = brandGreen,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { 
                    onMarkAllRead() 
                }
            )
        }
        
        HorizontalDivider(thickness = 1.dp, color = dividerColor)

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada notifikasi baru",
                    fontSize = 16.sp,
                    fontFamily = InterFamily,
                    color = textColorSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(notifications) { item ->
                    NotificationRow(
                        item = item, 
                        brandGreen = brandGreen, 
                        textColorPrimary = textColorPrimary, 
                        textColorSecondary = textColorSecondary,
                        onClick = { 
                            onNotificationClick(item) 
                        }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = dividerColor.copy(alpha = 0.5f))
                }
            }
        }

        // Bottom Handle for Swipe Up
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(swipeModifier)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(dividerColor)
            )
        }
    }
}

@Composable
fun NotificationRow(
    item: NotifikasiItem,
    brandGreen: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon with light green background
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5EE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = brandGreen,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFamily,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.message,
                fontSize = 13.sp,
                fontFamily = InterFamily,
                color = textColorSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.time,
                fontSize = 12.sp,
                fontFamily = InterFamily,
                color = Color(0xFF8CA196)
            )
        }
        
        if (item.isUnread) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(brandGreen)
            )
        }
    }
}
