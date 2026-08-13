package com.example.baper_andoid.ui.screen.profil

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfilViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    private val _profileImageUri = mutableStateOf<Uri?>(null)
    val profileImageUri: State<Uri?> = _profileImageUri

    private val _nama = mutableStateOf("Memuat...")
    val nama: State<String> = _nama

    private val _businessName = mutableStateOf("Memuat...")
    val businessName: State<String> = _businessName

    private val _email = mutableStateOf("Memuat...")
    val email: State<String> = _email

    private val _noTelepon = mutableStateOf("-")
    val noTelepon: State<String> = _noTelepon

    private val _alamat = mutableStateOf("-")
    val alamat: State<String> = _alamat
    
    // Statistik Data
    private val _totalPesanan = mutableStateOf("0")
    val totalPesanan: State<String> = _totalPesanan

    private val _pelanggan = mutableStateOf("0")
    val pelanggan: State<String> = _pelanggan

    private val _tanggalBergabung = mutableStateOf("-")
    val tanggalBergabung: State<String> = _tanggalBergabung

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val response = profileRepository.getProfile()
                if (response.status && response.data != null) {
                    val data = response.data
                    _nama.value = data.name
                    _businessName.value = data.businessName
                    _email.value = data.email
                    _noTelepon.value = data.phone
                    _alamat.value = data.address
                    _totalPesanan.value = data.totalOrders.toString()
                    _pelanggan.value = data.totalCustomers.toString()
                    _tanggalBergabung.value = data.joinedDate
                }
            } catch (e: Exception) {
                // Biarkan default kalau gagal
            }
        }
    }

    fun updateProfil(nama: String, email: String, noTelepon: String, alamat: String) {
        // Ini cuma update UI lokal, karena edit profile ke backend belum ada API-nya
        _nama.value = nama
        _email.value = email
        _noTelepon.value = noTelepon
        _alamat.value = alamat
    }

    fun updateProfileImage(uri: Uri?) {
        _profileImageUri.value = uri
    }

    fun removeProfileImage() {
        _profileImageUri.value = null
    }
}
