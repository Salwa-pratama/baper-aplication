package com.example.baper_andoid.ui.screen.bot

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baper_andoid.data.repository.BotRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BotViewModel(private val repository: BotRepository) : ViewModel() {
    private val _isBotActive = mutableStateOf(false)
    val isBotActive: State<Boolean> = _isBotActive

    private val _botPrompt = mutableStateOf("")
    val botPrompt: State<String> = _botPrompt

    private val _apiKey = mutableStateOf("")
    val apiKey: State<String> = _apiKey

    private val _botNumber = mutableStateOf("")
    val botNumber: State<String> = _botNumber

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private var botId: String = ""

    private val _botLogs = mutableStateListOf<BotLog>().apply {
        addAll(listOf(
            BotLog("System", "Menunggu sinkronisasi dengan server...", getCurrentTime())
        ))
    }
    val botLogs: List<BotLog> = _botLogs

    init {
        fetchMyBot()
    }

    fun fetchMyBot(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getMyBot()
                if (response.status && response.data != null) {
                    botId = response.data.id
                    _isBotActive.value = response.data.isActive
                    _botPrompt.value = response.data.agentPrompt
                    _apiKey.value = response.data.agentApi
                    addLog("System", "Berhasil memuat konfigurasi bot")
                } else {
                    addLog("System", "Gagal memuat konfigurasi bot: ${response.message}")
                }
            } catch (e: Exception) {
                addLog("System", "Error koneksi server: ${e.message}")
            } finally {
                _isLoading.value = false
                onComplete?.invoke()
            }
        }
    }

    fun toggleBot() {
        if (botId.isEmpty()) {
            addLog("System", "Bot ID tidak ditemukan, tunggu sinkronisasi")
            return
        }
        viewModelScope.launch {
            try {
                val response = repository.toggleBotStatus(botId)
                if (response.status) {
                    _isBotActive.value = !_isBotActive.value
                    val action = if (_isBotActive.value) "diaktifkan" else "dimatikan"
                    addLog("User", "Bot berhasil $action")
                } else {
                    addLog("System", "Gagal toggle bot: ${response.message}")
                }
            } catch (e: Exception) {
                addLog("System", "Gagal memproses ke server")
            }
        }
    }

    fun onPromptChange(newValue: String) {
        _botPrompt.value = newValue
    }

    fun onApiKeyChange(newValue: String) {
        _apiKey.value = newValue
    }

    fun onBotNumberChange(newValue: String) {
        _botNumber.value = newValue
    }

    fun saveBotConfig() {
        if (botId.isEmpty()) {
            addLog("System", "Bot ID tidak ditemukan")
            return
        }
        viewModelScope.launch {
            try {
                val response = repository.updateBotPrompt(botId, _apiKey.value, _botPrompt.value)
                if (response.status) {
                    addLog("User", "Konfigurasi (API & Prompt) berhasil disimpan")
                } else {
                    addLog("System", "Gagal menyimpan: ${response.message}")
                }
            } catch (e: Exception) {
                addLog("System", "Gagal tersambung ke server")
            }
        }
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
