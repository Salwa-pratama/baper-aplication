package com.example.baper_andoid.ui.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*


@Composable
fun BaperSplashScreen(composition: LottieComposition?, onFinished: () -> Unit) {
    // Mulai dari frame 72 (awal fade-in) sampai selesai
    val progressState = animateLottieCompositionAsState(
        composition = composition,
        // Mulai dari awal (frame 0) agar seluruh rangkaian animasi terlihat
        iterations = 1,
        isPlaying = true,
        speed = 0.5f
    )

    LaunchedEffect(progressState.value) {
        if (progressState.value >= 1f) {
            onFinished() // Langsung pindah setelah animasi selesai tanpa delay tambahan
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                // Menggunakan progress secara langsung dari state pemutaran
                progress = { progressState.value },
                modifier = Modifier.size(280.dp)
            )
        }
        // Tidak ada lagi CircularProgressIndicator atau Box kosong yang lama
    }
}
