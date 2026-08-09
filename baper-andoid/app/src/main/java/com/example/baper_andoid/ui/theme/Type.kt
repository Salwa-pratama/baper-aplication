package com.example.baper_andoid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.baper_andoid.R

// Konfigurasi Google Font Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Definisi Font Inter (Modern, Bersih, & Minimalis)
val InterFont = GoogleFont("Inter")
val InterFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.ExtraBold)
)

// Alias untuk Tipografi Minimalis
val TitleFontFamily = InterFamily
val BodyFontFamily = InterFamily

val Typography = Typography(
    // Style untuk Judul Besar (Onboarding)
    displayMedium = TextStyle(
        fontFamily = TitleFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    // Style untuk Judul Section
    headlineSmall = TextStyle(
        fontFamily = TitleFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
    ),
    // Style untuk Teks Utama (Isi)
    bodyLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp, // Memberikan ruang napas agar tidak kaku
        letterSpacing = 0.1.sp
    ),
    // Style untuk Teks Pendukung (Deskripsi)
    bodyMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    // Style untuk Tombol & Label
    labelLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
