package com.example.baper_andoid.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.baper_andoid.R
import com.example.baper_andoid.data.local.UserPreferences
import com.example.baper_andoid.ui.screen.splash.BaperSplashScreen
import com.example.baper_andoid.ui.screen.onboarding.BaperOnboardingScreen
import com.example.baper_andoid.ui.screen.login.LoginScreen
import com.example.baper_andoid.ui.screen.register.RegisterScreen
import com.example.baper_andoid.ui.screen.home.HomeScreen
import com.example.baper_andoid.ui.screen.chat.ChatScreen
import com.example.baper_andoid.ui.screen.chat.ChatViewModel
import com.example.baper_andoid.ui.screen.chat.ChatViewModelFactory
import com.example.baper_andoid.data.repository.ChatRepository
import com.example.baper_andoid.data.remote.RetrofitClient
import com.example.baper_andoid.ui.screen.bot.BotViewModel
import com.example.baper_andoid.ui.screen.bot.BotStatusScreen
import com.example.baper_andoid.ui.screen.profil.ProfilViewModel
import com.example.baper_andoid.ui.screen.lihatpesanan.LihatPesananScreen
import com.example.baper_andoid.ui.screen.lihatpesanan.LihatPesananViewModel
import com.example.baper_andoid.ui.screen.rekap.RekapDetailScreen
import com.example.baper_andoid.ui.screen.notifikasi.NotifikasiScreen
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
    
    val chatRepository = remember { ChatRepository(RetrofitClient.getInstance(context)) }
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(chatRepository))
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
                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                },
                onNavigateToBotStatus = {
                    navController.navigate(Screen.BotStatus.route)
                },
                onNavigateToLihatPesanan = { tabIndex ->
                    navController.navigate(Screen.LihatPesanan.createRoute(tabIndex))
                },
                onNavigateToRekapDetail = { month ->
                    navController.navigate(Screen.RekapDetail.createRoute(month))
                },
                onNavigateToNotifikasi = {
                    navController.navigate(Screen.Notifikasi.route)
                }
            )
        }

        composable(Screen.Notifikasi.route) {
            NotifikasiScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            ChatScreen(
                chatViewModel = chatViewModel,
                sessionId = sessionId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BotStatus.route) { _ ->
            BotStatusScreen(
                viewModel = botViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LihatPesanan.route,
            arguments = listOf(
                navArgument("tabIndex") { 
                    type = NavType.IntType
                    defaultValue = 0 
                }
            )
        ) { backStackEntry ->
            val tabIndex = backStackEntry.arguments?.getInt("tabIndex") ?: 0
            LihatPesananScreen(
                viewModel = lihatPesananViewModel,
                initialTab = tabIndex,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { chatId ->
                    chatViewModel.markAsRead(chatId)
                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                }
            )
        }

        composable(
            route = Screen.RekapDetail.route,
            arguments = listOf(navArgument("month") { type = NavType.StringType })
        ) { backStackEntry ->
            val month = backStackEntry.arguments?.getString("month") ?: ""
            RekapDetailScreen(
                month = month,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { chatId ->
                    chatViewModel.markAsRead(chatId)
                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                }
            )
        }
    }
}
