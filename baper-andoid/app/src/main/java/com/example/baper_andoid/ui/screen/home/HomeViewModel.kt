package com.example.baper_andoid.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.repository.HomeRepository
import com.example.baper_andoid.data.repository.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val stats: Map<String, String> = emptyMap(),
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null
)

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            fetchData()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            delay(1500) // Simulasi loading
            fetchData()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    private suspend fun fetchData() {
        try {
            val stats = repository.getDashboardStats()
            val transactions = repository.getRecentTransactions()
            _uiState.value = _uiState.value.copy(
                stats = stats,
                transactions = transactions,
                error = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = e.localizedMessage)
        }
    }
}
