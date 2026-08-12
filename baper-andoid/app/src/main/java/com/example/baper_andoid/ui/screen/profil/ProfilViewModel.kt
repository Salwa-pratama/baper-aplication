package com.example.baper_andoid.ui.screen.profil

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ProfilViewModel : ViewModel() {
    private val _profileImageUri = mutableStateOf<Uri?>(null)
    val profileImageUri: State<Uri?> = _profileImageUri

    private val _nama = mutableStateOf("Ahmad Santoso")
    val nama: State<String> = _nama

    private val _email = mutableStateOf("ahmad.santoso@email.com")
    val email: State<String> = _email

    private val _noTelepon = mutableStateOf("+62 812-3456-7890")
    val noTelepon: State<String> = _noTelepon

    private val _alamat = mutableStateOf("Jl. Diponegoro No. 22")
    val alamat: State<String> = _alamat

    // Statistik Data
    private val _totalPesanan = mutableStateOf("156")
    val totalPesanan: State<String> = _totalPesanan

    private val _pelanggan = mutableStateOf("42")
    val pelanggan: State<String> = _pelanggan

    private val _tanggalBergabung = mutableStateOf("Jan 2024")
    val tanggalBergabung: State<String> = _tanggalBergabung

    fun updateProfil(nama: String, email: String, noTelepon: String, alamat: String) {
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
