package com.example.baper_andoid.ui.screen.produk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.remote.dto.response.ProductItem
import com.example.baper_andoid.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.baper_andoid.utils.getErrorMessage

data class ProdukUiState(
    val isLoading: Boolean = false,
    val productList: List<ProductItem> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false
)

class ProdukViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProdukUiState(
        productList = emptyList()
    ))
    val uiState: StateFlow<ProdukUiState> = _uiState.asStateFlow()

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = repository.getProducts()
                if (response.status) {
                    _uiState.value = _uiState.value.copy(
                        productList = response.data,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    productList = emptyList(),
                    error = e.getErrorMessage("Gagal mengambil data produk")
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun addProduct(name: String, description: String, price: Int, stock: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isSuccess = false)
            try {
                val response = repository.createProduct(name, description, price, stock)
                if (response.status) {
                    _uiState.value = _uiState.value.copy(isSuccess = true, error = null)
                    getProducts() // Refresh list
                } else {
                    _uiState.value = _uiState.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.getErrorMessage("Gagal menambah produk"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateProduct(id: String, name: String, description: String, price: Int, stock: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isSuccess = false)
            try {
                val response = repository.updateProduct(id, name, description, price, stock)
                if (response.status) {
                    _uiState.value = _uiState.value.copy(isSuccess = true, error = null)
                    getProducts() // Refresh list
                } else {
                    _uiState.value = _uiState.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.getErrorMessage("Gagal memperbarui produk"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = repository.deleteProduct(id)
                if (response.status) {
                    getProducts() // Refresh list
                } else {
                    _uiState.value = _uiState.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.getErrorMessage("Gagal menghapus produk"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
