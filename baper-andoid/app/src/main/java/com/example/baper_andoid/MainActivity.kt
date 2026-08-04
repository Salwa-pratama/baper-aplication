package com.example.baper_andoid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.example.baper_andoid.ui.theme.BaperandoidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaperandoidTheme {
                val currentScreen = remember { mutableStateOf("splash") }

                // Pre-load Lottie di tingkat teratas agar dimulai secepat mungkin
                val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo_vectorized))
                val composition = compositionResult.value

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (currentScreen.value == "splash") {
                        BaperSplashScreen(
                            composition = composition,
                            onFinished = { currentScreen.value = "onboarding" }
                        )
                    } else {
                        BaperOnboardingScreen(composition = composition)
                    }
                }
            }
        }
    }
}

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

@Composable
fun BaperOnboardingScreen(composition: LottieComposition?) {
    val pages = listOf(
        BaperPageData("Pesan Lebih Cepat", "Sistem otomatis kami menangkap pesanan pelanggan Anda dalam hitungan detik."),
        BaperPageData("Rekap Tanpa Pusing", "Semua data transaksi tersimpan rapi dan aman, bisa diakses kapan saja."),
        BaperPageData("Siap Jualan?", "Mulai kelola bisnis Anda dengan profesional mulai hari ini.")
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val brandGreen = Color(0xFF28A745)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                        if (composition != null) {
                            val progressState = animateLottieCompositionAsState(
                                composition = composition,
                                // Mulai dari awal (frame 0) agar animasi awal tidak terlewat
                                iterations = LottieConstants.IterateForever,
                                isPlaying = true,
                                speed = 0.5f
                            )
                            LottieAnimation(
                                composition = composition,
                                progress = { progressState.value },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            CircularProgressIndicator(color = brandGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = pages[page].title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = pages[page].description,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        lineHeight = 24.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    repeat(pages.size) { i ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (pagerState.currentPage == i) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (pagerState.currentPage == i) brandGreen else Color.LightGray)
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            if(pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (pagerState.currentPage == pages.size - 1) "Mulai" else "Lanjut")
                }
            }
        }
    }
}

data class BaperPageData(val title: String, val description: String)
