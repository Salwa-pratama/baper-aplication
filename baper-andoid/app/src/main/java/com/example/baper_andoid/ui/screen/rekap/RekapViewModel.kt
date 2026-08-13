package com.example.baper_andoid.ui.screen.rekap

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.remote.dto.response.OrderResponse
import com.example.baper_andoid.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class RekapViewModel(private val repository: OrderRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> get() = _orders
    
    private val _rekapDataMap = MutableStateFlow<Map<String, List<RekapData>>>(emptyMap())
    val rekapDataMap: StateFlow<Map<String, List<RekapData>>> get() = _rekapDataMap

    data class MonthSummary(
        val totalRevenue: String = "Rp 0",
        val paidCount: Int = 0,
        val pendingCount: Int = 0
    )

    private val _currentMonthSummary = MutableStateFlow(MonthSummary())
    val currentMonthSummary: StateFlow<MonthSummary> get() = _currentMonthSummary

    private val _availableYears = MutableStateFlow<List<String>>(listOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR).toString()))
    val availableYears: StateFlow<List<String>> get() = _availableYears

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getOrders()
                if (response.status) {
                    val ordersList = response.data ?: emptyList()
                    _orders.value = ordersList
                    
                    // Extract dynamic years
                    val years = ordersList.mapNotNull { order ->
                        val parts = order.date.split(" ")
                        if (parts.isNotEmpty()) parts.last() else null
                    }.distinct().filter { it.length == 4 && it.all { char -> char.isDigit() } }.sortedDescending()
                    
                    if (years.isNotEmpty()) {
                        _availableYears.value = years
                    }
                    
                    processRecapData(ordersList)
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Gagal mengambil data rekap"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun processRecapData(orders: List<OrderResponse>) {
        val monthNames = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        
        val map = mutableMapOf<String, List<RekapData>>()
        val yearsToProcess = _availableYears.value
        
        for (year in yearsToProcess) {
            val yearlyRekap = mutableListOf<RekapData>()
            // Iterate months backwards (December to January)
            for (month in monthNames.reversed()) {
                val keyword = "$month $year"
                
                // Filter paid orders in this month and year
                val paidOrdersInMonth = orders.filter { 
                    it.date.contains(keyword, ignoreCase = true) && it.status == "Sudah Lunas" 
                }
                
                // Only add to the list if there's data, or just show it anyway (the dummy showed even if empty)
                // We'll calculate the total amount
                val totalAmount = paidOrdersInMonth.sumOf { 
                    val amt = it.amount.replace("Rp ", "").replace(".", "")
                    amt.toIntOrNull() ?: 0
                }
                
                // We only include the month if it has any paid orders
                if (paidOrdersInMonth.isNotEmpty()) {
                    yearlyRekap.add(
                        RekapData(
                            id = "REKAP-${month.take(5).uppercase()}-$year",
                            month = month,
                            description = "Cek pendapatan anda selama bulan ${month.lowercase()}",
                            amount = "Rp ${String.format(Locale("in", "ID"), "%,d", totalAmount).replace(",", ".")}"
                        )
                    )
                }
            }
            map[year] = yearlyRekap
        }
        
        _rekapDataMap.value = map

        // Calculate for Current Month Summary (Let's use the first month that has data in the current year, or just the current Calendar month)
        // For simplicity, we can get the actual current month/year:
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR).toString()
        val currentMonthIndex = calendar.get(java.util.Calendar.MONTH) // 0-based
        val currentMonthName = monthNames[currentMonthIndex]
        val currentKeyword = "$currentMonthName $currentYear"

        val currentOrders = orders.filter { it.date.contains(currentKeyword, ignoreCase = true) }
        val paidCount = currentOrders.count { it.status == "Sudah Lunas" }
        val pendingCount = currentOrders.count { it.status == "Belum Bayar" }
        val currentRevenue = currentOrders.filter { it.status == "Sudah Lunas" }.sumOf { 
            val amt = it.amount.replace("Rp ", "").replace(".", "")
            amt.toIntOrNull() ?: 0
        }

        _currentMonthSummary.value = MonthSummary(
            totalRevenue = "Rp ${String.format(Locale("in", "ID"), "%,d", currentRevenue).replace(",", ".")}",
            paidCount = paidCount,
            pendingCount = pendingCount
        )
    }

    // Get specific orders for RekapDetailScreen
    fun getPaidOrdersForMonth(month: String, year: String): List<OrderResponse> {
        val keyword = "$month $year"
        return _orders.value.filter { 
            it.date.contains(keyword, ignoreCase = true) && it.status == "Sudah Lunas" 
        }
    }

    fun downloadRekap(context: Context, year: String, monthName: String) {
        viewModelScope.launch {
            try {
                Toast.makeText(context, "Mempersiapkan unduhan...", Toast.LENGTH_SHORT).show()
                val monthNames = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
                val monthInt = monthNames.indexOf(monthName) + 1
                if (monthInt == 0) {
                    Toast.makeText(context, "Bulan tidak valid", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val response = repository.exportMonthlyRecap(year.toInt(), monthInt)
                val fileName = "Rekap_Bulanan_${monthName}_${year}.csv"
                
                withContext(Dispatchers.IO) {
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadsDir, fileName)
                    
                    response.byteStream().use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                Toast.makeText(context, "Berhasil diunduh ke folder Downloads", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengunduh: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
