package com.example.baper_andoid.ui.screen.bot

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class BotViewModel : ViewModel() {
    private val _isBotActive = mutableStateOf(true)
    val isBotActive: State<Boolean> = _isBotActive

    private val _botPrompt = mutableStateOf("")
    val botPrompt: State<String> = _botPrompt

    private val _apiKey = mutableStateOf("")
    val apiKey: State<String> = _apiKey

    private val _botLogs = mutableStateListOf<BotLog>().apply {
        addAll(listOf(
            BotLog("System", "Bot diaktifkan pertama kali", getCurrentTime()),
            BotLog("System", "Koneksi ke server stabil", getCurrentTime())
        ))
    }
    val botLogs: List<BotLog> = _botLogs

    fun toggleBot() {
        _isBotActive.value = !_isBotActive.value
        val action = if (_isBotActive.value) "diaktifkan" else "dimatikan"
        addLog("User", "Bot berhasil $action")
    }

    fun onPromptChange(newValue: String) {
        _botPrompt.value = newValue
    }

    fun onApiKeyChange(newValue: String) {
        _apiKey.value = newValue
    }

    fun saveApiKey() {
        addLog("User", "API Key berhasil disimpan")
        // Logic Simpan ke DataStore/SharedPrefs nanti
    }

    private fun addLog(sender: String, message: String) {
        _botLogs.add(0, BotLog(sender, message, getCurrentTime()))
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
}

data class BotLog(
    val sender: String,
    val message: String,
    val time: String
)
