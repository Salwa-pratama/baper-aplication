package com.example.baper_andoid.data.repository

import com.example.baper_andoid.data.remote.ApiService

class HomeRepository(private val apiService: ApiService) {
    // Simulasi ambil data dashboard
    suspend fun getDashboardStats() = mapOf(
        "omzet" to "Rp 2.500.000",
        "pesanan" to "12",
        "rekap" to "8"
    )

    suspend fun getRecentTransactions() = listOf(
        Transaction("TRX-001", "Pesan Kopi Susu", "Rp 25.000", "Sukses"),
        Transaction("TRX-002", "Pesan Nasi Goreng", "Rp 15.000", "Pending"),
        Transaction("TRX-003", "Rekap Harian", "Rp 500.000", "Sukses")
    )
}

data class Transaction(
    val id: String,
    val title: String,
    val amount: String,
    val status: String
)
