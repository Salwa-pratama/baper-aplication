package com.example.baper_andoid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.baper_andoid.ui.screen.splash.BaperSplashScreen
import com.example.baper_andoid.ui.screen.onboarding.BaperOnboardingScreen
import com.example.baper_andoid.ui.screen.login.LoginScreen
import com.example.baper_andoid.ui.screen.register.RegisterScreen
import com.example.baper_andoid.ui.screen.home.HomeScreen
import com.example.baper_andoid.ui.screen.chat.ChatScreen
import com.example.baper_andoid.ui.screen.chat.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.baper_andoid.R

@Composable
fun NavGraph(navController: NavHostController) {
    // Load Lottie composition sekali saja untuk digunakan di Splash & Onboarding
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo_vectorized))
    
    // ViewModel tunggal untuk fitur Chat (Shared)
    val chatViewModel: ChatViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        // ... (rest of the routes)
        composable(Screen.Splash.route) {
            BaperSplashScreen(
                composition = composition,
                onFinished = {
                    navController.navigate(Screen.OnBoarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OnBoarding.route) {
            BaperOnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.OnBoarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                chatViewModel = chatViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToChat = { chatId ->
                    chatViewModel.markAsRead(chatId) // Hapus badge saat dibuka
                    navController.navigate(Screen.ChatDetail.route)
                }
            )
        }

        composable(Screen.ChatDetail.route) {
            ChatScreen(
                chatViewModel = chatViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
