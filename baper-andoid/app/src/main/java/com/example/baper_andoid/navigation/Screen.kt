package com.example.baper_andoid.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object OnBoarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ChatDetail : Screen("chat_detail")
    object BotStatus : Screen("bot_status")
}