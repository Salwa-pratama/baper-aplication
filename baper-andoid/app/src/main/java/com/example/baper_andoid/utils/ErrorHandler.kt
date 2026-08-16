package com.example.baper_andoid.utils

import org.json.JSONObject
import retrofit2.HttpException

/**
 * Ekstensi untuk mengambil pesan error dari backend kalau terjadi HttpException (misal 400, 401, 404).
 * @param defaultMessage Pesan default yang dikembalikan kalau exception bukan dari HTTP atau gagal diparsing.
 */
fun Exception.getErrorMessage(defaultMessage: String = "Terjadi kesalahan koneksi"): String {
    if (this is HttpException) {
        return try {
            val errorBody = this.response()?.errorBody()?.string()
            val jsonObject = JSONObject(errorBody ?: "")
            jsonObject.getString("message")
        } catch (ex: Exception) {
            this.localizedMessage ?: defaultMessage
        }
    }
    return this.localizedMessage ?: defaultMessage
}
