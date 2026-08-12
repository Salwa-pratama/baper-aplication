package com.example.baper_andoid.ui.screen.produk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.remote.dto.response.ProductItem
import com.example.baper_andoid.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProdukUiState(
    val isLoading: Boolean = false,
    val productList: List<ProductItem> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false
)

class ProdukViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProdukUiState(
        productList = listOf(
            ProductItem("dummy1", "biz1", "Contoh Produk A", "Deskripsi singkat produk contoh A", 125000, 48),
            ProductItem("dummy2", "biz1", "Contoh Produk B", "Deskripsi singkat produk contoh B", 75000, 120)
        )
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
                    // Selalu tambahkan dummy di awal untuk preview, gabungkan dengan data real
                    val dummyData = listOf(
                        ProductItem("dummy1", "biz1", "Contoh Produk A", "Deskripsi singkat produk contoh A", 125000, 48),
                        ProductItem("dummy2", "biz1", "Contoh Produk B", "Deskripsi singkat produk contoh B", 75000, 120)
                    )
                    _uiState.value = _uiState.value.copy(
                        productList = dummyData + response.data,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                // Jika error (misal belum ada internet), tetap tampilkan dummy agar UI bisa dilihat
                val dummyData = listOf(
                    ProductItem("dummy1", "biz1", "Contoh Produk A", "Deskripsi singkat produk contoh A", 125000, 48),
                    ProductItem("dummy2", "biz1", "Contoh Produk B", "Deskripsi singkat produk contoh B", 75000, 120)
                )
                _uiState.value = _uiState.value.copy(
                    productList = dummyData,
                    error = "Gagal mengambil data real, menampilkan data contoh."
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
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Gagal menambah produk")
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
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Gagal memperbarui produk")
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
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Gagal menghapus produk")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
