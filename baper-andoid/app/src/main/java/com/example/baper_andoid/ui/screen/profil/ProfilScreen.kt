package com.example.baper_andoid.ui.screen.profil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.baper_andoid.ui.theme.InterFamily
import com.yalantis.ucrop.UCrop
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    viewModel: ProfilViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val textColorPrimary = Color(0xFF0F172A)
    val textColorSecondary = Color(0xFF64748B)

    // Bottom Sheet State
    var showPhotoSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Konfigurasi warna seleksi teks
    val customTextSelectionColors = TextSelectionColors(
        handleColor = brandGreen,
        backgroundColor = brandGreen.copy(alpha = 0.2f)
    )

    // Data dari ViewModel
    val nama by viewModel.nama
    val email by viewModel.email
    val noTelepon by viewModel.noTelepon
    val alamat by viewModel.alamat
    val profileImageUri by viewModel.profileImageUri
    val totalPesanan by viewModel.totalPesanan
    val pelanggan by viewModel.pelanggan
    val tanggalBergabung by viewModel.tanggalBergabung

    // Launcher untuk uCrop
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    viewModel.updateProfileImage(resultUri)
                }
            }
        }
    )

    // Image Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                // Menyiapkan nama file unik agar Coil mendeteksi perubahan gambar (mencegah cache issue)
                val fileName = "profile_crop_${System.currentTimeMillis()}.jpg"
                val destinationUri = android.net.Uri.fromFile(File(context.cacheDir, fileName))
                
                // Konfigurasi uCrop (Tema Hijau BAPER + Circular)
                val options = UCrop.Options().apply {
                    setCompressionQuality(80)
                    setToolbarColor(brandGreen.toArgb())
                    setToolbarWidgetColor(Color.White.toArgb())
                    setCircleDimmedLayer(true) // Efek lingkaran
                    setShowCropGrid(false)
                    setShowCropFrame(false)
                }

                val uCropIntent = UCrop.of(uri, destinationUri)
                    .withAspectRatio(1f, 1f) // Rasio kotak
                    .withOptions(options)
                    .getIntent(context)

                cropLauncher.launch(uCropIntent)
            }
            showPhotoSheet = false
        }
    )

    // Local State for Inline Editing
    var isEditing by remember { mutableStateOf(false) }
    var editNama by remember { mutableStateOf(nama) }
    var editEmail by remember { mutableStateOf(email) }
    var editNoTelepon by remember { mutableStateOf(noTelepon) }
    var editAlamat by remember { mutableStateOf(alamat) }

    LaunchedEffect(nama, email, noTelepon, alamat) {
        if (!isEditing) {
            editNama = nama
            editEmail = email
            editNoTelepon = noTelepon
            editAlamat = alamat
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brandGreen)
    ) {
        ProfileHeader(
            brandGreen = brandGreen,
            nama = nama,
            imageUri = profileImageUri,
            onImageClick = { showPhotoSheet = true }
        )

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = bgGray,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        StatsCard(
                            brandGreen = brandGreen,
                            textColorSecondary = textColorSecondary,
                            totalPesanan = totalPesanan,
                            pelanggan = pelanggan,
                            tanggalBergabung = tanggalBergabung
                        )
                    }

                    item {
                        AccountInfoCard(
                            brandGreen = brandGreen,
                            textColorPrimary = textColorPrimary,
                            textColorSecondary = textColorSecondary,
                            isEditing = isEditing,
                            nama = if (isEditing) editNama else nama,
                            email = if (isEditing) editEmail else email,
                            noTelepon = if (isEditing) editNoTelepon else noTelepon,
                            alamat = if (isEditing) editAlamat else alamat,
                            onNamaChange = { editNama = it },
                            onEmailChange = { editEmail = it },
                            onNoTeleponChange = { editNoTelepon = it },
                            onAlamatChange = { editAlamat = it },
                            onEditToggle = {
                                if (isEditing) {
                                    editNama = nama
                                    editEmail = email
                                    editNoTelepon = noTelepon
                                    editAlamat = alamat
                                }
                                isEditing = !isEditing
                            },
                            onSave = {
                                viewModel.updateProfil(editNama, editEmail, editNoTelepon, editAlamat)
                                isEditing = false
                            }
                        )
                    }

                    item {
                        LogoutCard(onLogout)
                    }
                }
            }
        }
    }

    if (showPhotoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            PhotoActionContent(
                onPickImage = {
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onDeleteImage = {
                    viewModel.removeProfileImage()
                    showPhotoSheet = false
                },
                showDeleteOption = profileImageUri != null,
                brandGreen = brandGreen
            )
        }
    }
}

@Composable
fun ProfileHeader(
    brandGreen: Color,
    nama: String,
    imageUri: android.net.Uri?,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brandGreen)
            .systemBarsPadding()
            .padding(vertical = 24.dp, horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Container Utama Foto
            Box(
                modifier = Modifier.size(84.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background & Foto Profil dengan Klik Lingkaran
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onImageClick() }, // Klik pada foto
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                // Ikon Kamera dengan Klik Lingkaran (Tetap di pojok)
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        color = Color.White,
                        shape = CircleShape,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onImageClick() } // Klik pada ikon kamera
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = brandGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = nama, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = InterFamily)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text(text = "Reseller - Bandung", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = Color.White, fontSize = 12.sp, fontFamily = InterFamily)
                }
            }
        }
    }
}

@Composable
fun StatsCard(brandGreen: Color, textColorSecondary: Color, totalPesanan: String, pelanggan: String, tanggalBergabung: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            StatItem("TOTAL PESANAN", totalPesanan, brandGreen, textColorSecondary)
            VerticalDivider(modifier = Modifier.height(40.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))
            StatItem("PELANGGAN", pelanggan, brandGreen, textColorSecondary)
            VerticalDivider(modifier = Modifier.height(40.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))
            StatItem("BERGABUNG", tanggalBergabung, brandGreen, textColorSecondary)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, brandGreen: Color, labelColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = labelColor, fontFamily = InterFamily)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = brandGreen, fontFamily = InterFamily)
    }
}

@Composable
fun AccountInfoCard(brandGreen: Color, textColorPrimary: Color, textColorSecondary: Color, isEditing: Boolean, nama: String, email: String, noTelepon: String, alamat: String, onNamaChange: (String) -> Unit, onEmailChange: (String) -> Unit, onNoTeleponChange: (String) -> Unit, onAlamatChange: (String) -> Unit, onEditToggle: () -> Unit, onSave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "INFORMASI AKUN", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = brandGreen, fontFamily = InterFamily, letterSpacing = 0.5.sp)
                if (isEditing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(onClick = onEditToggle, modifier = Modifier.size(20.dp)) { Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red) }
                        IconButton(onClick = onSave, modifier = Modifier.size(20.dp)) { Icon(Icons.Default.Check, contentDescription = null, tint = brandGreen) }
                    }
                } else {
                    IconButton(onClick = onEditToggle, modifier = Modifier.size(20.dp)) { Icon(Icons.Default.Edit, contentDescription = null, tint = brandGreen) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow("Nama Lengkap", nama, textColorPrimary, textColorSecondary, isEditing, onNamaChange)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))
            InfoRow("Email", email, textColorPrimary, textColorSecondary, isEditing, onEmailChange)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))
            InfoRow("No. Telepon", noTelepon, textColorPrimary, textColorSecondary, isEditing, onNoTeleponChange)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))
            InfoRow("Alamat", alamat, textColorPrimary, textColorSecondary, isEditing, onAlamatChange)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, textColorPrimary: Color, textColorSecondary: Color, isEditing: Boolean = false, onValueChange: (String) -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 14.sp, color = textColorPrimary, fontFamily = InterFamily, fontWeight = FontWeight.Medium)
        if (isEditing) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f).padding(start = 16.dp),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = InterFamily, textAlign = TextAlign.End, fontWeight = FontWeight.Bold),
                cursorBrush = SolidColor(Color(0xFF107C42)),
                singleLine = true
            )
        } else {
            Text(text = value, fontSize = 14.sp, color = textColorSecondary, fontFamily = InterFamily, textAlign = TextAlign.End, modifier = Modifier.weight(1f).padding(start = 16.dp))
        }
    }
}

@Composable
fun LogoutCard(onLogout: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onLogout() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Keluar", color = Color(0xFFDC3545), fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = InterFamily)
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFDC3545), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun PhotoActionContent(onPickImage: () -> Unit, onDeleteImage: () -> Unit, showDeleteOption: Boolean, brandGreen: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 8.dp, start = 24.dp, end = 24.dp)) {
        Text(
            text = "Foto Profil", 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold, 
            fontFamily = InterFamily, 
            color = Color.Black,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().clickable { onPickImage() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = brandGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Pilih dari Galeri", fontSize = 16.sp, fontFamily = InterFamily, color = Color.Black)
        }
        if (showDeleteOption) {
            Row(modifier = Modifier.fillMaxWidth().clickable { onDeleteImage() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Hapus Foto", fontSize = 16.sp, fontFamily = InterFamily, color = Color.Red)
            }
        }
    }
}
