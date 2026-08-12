package com.example.baper_andoid.ui.screen.lihatpesanan

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Order(
    val id: String,
    val customerName: String,
    val packageName: String,
    val date: String,
    val amount: String,
    var status: String, // "Belum Bayar" or "Sudah Lunas"
    val chatId: String // Target chat ID
)

class LihatPesananViewModel : ViewModel() {
    private val _orders = mutableStateListOf(
        Order("#ORD-2026-001", "Ahmad Rizky", "Paket Hemat x2", "5 Agustus 2026", "Rp 150.000", "Belum Bayar", "1"),
        Order("#ORD-2026-002", "Siti Maesaroh", "Paket Premium x1", "5 Agustus 2026", "Rp 220.000", "Belum Bayar", "2"),
        Order("#ORD-2026-003", "Budi Pratama", "Paket Lengkap x1", "4 Agustus 2026", "Rp 150.000", "Belum Bayar", "3"),
        Order("#ORD-2026-004", "Dewi Sartika", "Paket Hemat x1", "3 Agustus 2026", "Rp 75.000", "Sudah Lunas", "4"),
        Order("#ORD-2026-005", "Rian Hidayat", "Paket Premium x2", "2 Agustus 2026", "Rp 440.000", "Sudah Lunas", "5")
    )
    val orders: List<Order> get() = _orders

    fun confirmOrder(orderId: String) {
        val index = _orders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val updatedOrder = _orders[index].copy(status = "Sudah Lunas")
            _orders[index] = updatedOrder
        }
    }
}
