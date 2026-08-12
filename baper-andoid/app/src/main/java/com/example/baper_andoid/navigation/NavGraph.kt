package com.example.baper_andoid.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.baper_andoid.R
import com.example.baper_andoid.data.local.UserPreferences
import com.example.baper_andoid.ui.screen.splash.BaperSplashScreen
import com.example.baper_andoid.ui.screen.onboarding.BaperOnboardingScreen
import com.example.baper_andoid.ui.screen.login.LoginScreen
import com.example.baper_andoid.ui.screen.register.RegisterScreen
import com.example.baper_andoid.ui.screen.home.HomeScreen
import com.example.baper_andoid.ui.screen.chat.ChatScreen
import com.example.baper_andoid.ui.screen.chat.ChatViewModel
import com.example.baper_andoid.ui.screen.bot.BotViewModel
import com.example.baper_andoid.ui.screen.bot.BotStatusScreen
import com.example.baper_andoid.ui.screen.profil.ProfilViewModel
import com.example.baper_andoid.ui.screen.lihatpesanan.LihatPesananScreen
import com.example.baper_andoid.ui.screen.lihatpesanan.LihatPesananViewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo_vectorized))
    
    val chatViewModel: ChatViewModel = viewModel()
    val botViewModel: BotViewModel = viewModel()
    val profilViewModel: ProfilViewModel = viewModel()
    val lihatPesananViewModel: LihatPesananViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        
        composable(Screen.Splash.route) { _ ->
            BaperSplashScreen(
                composition = composition,
                onFinished = {
                    scope.launch {
                        val token = userPreferences.authToken.first()
                        if (!token.isNullOrEmpty()) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.OnBoarding.route) { _ ->
            BaperOnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.OnBoarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) { _ ->
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

        composable(Screen.Register.route) { _ ->
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) { _ ->
            HomeScreen(
                chatViewModel = chatViewModel,
                botViewModel = botViewModel,
                profilViewModel = profilViewModel,
                onLogout = {
                    scope.launch {
                        userPreferences.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavigateToChat = { chatId ->
                    chatViewModel.markAsRead(chatId)
                    navController.navigate(Screen.ChatDetail.route)
                },
                onNavigateToBotStatus = {
                    navController.navigate(Screen.BotStatus.route)
                },
                onNavigateToLihatPesanan = {
                    navController.navigate(Screen.LihatPesanan.route)
                }
            )
        }

        composable(Screen.ChatDetail.route) { _ ->
            ChatScreen(
                chatViewModel = chatViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BotStatus.route) { _ ->
            BotStatusScreen(
                viewModel = botViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LihatPesanan.route) { _ ->
            LihatPesananScreen(
                viewModel = lihatPesananViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { chatId ->
                    chatViewModel.markAsRead(chatId)
                    navController.navigate(Screen.ChatDetail.route)
                }
            )
        }
    }
}
