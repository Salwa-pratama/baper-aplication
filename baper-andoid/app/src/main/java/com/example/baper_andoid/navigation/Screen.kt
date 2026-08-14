package com.example.baper_andoid.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object OnBoarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ChatDetail : Screen("chat_detail/{sessionId}") {
        fun createRoute(sessionId: String) = "chat_detail/$sessionId"
    }
    object BotStatus : Screen("bot_status")
    object LihatPesanan : Screen("lihat_pesanan?tabIndex={tabIndex}") {
        fun createRoute(tabIndex: Int) = "lihat_pesanan?tabIndex=$tabIndex"
    }
    object RekapDetail : Screen("rekap_detail/{month}") {
        fun createRoute(month: String) = "rekap_detail/$month"
    }
}
