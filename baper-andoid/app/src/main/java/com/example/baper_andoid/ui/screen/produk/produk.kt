package com.example.baper_andoid.ui.screen.produk

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baper_andoid.data.remote.dto.response.ProductItem
import com.example.baper_andoid.ui.theme.InterFamily
import java.util.Locale
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukScreen(viewModel: ProdukViewModel) {
    val brandGreen = Color(0xFF107C42)
    val bgGray = Color(0xFFF7F9F8)
    val textColorSecondary = Color(0xFF64748B)
    
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    // State for Delete confirmation
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductItem?>(null) }

    // State for Add/Edit Product Sheet
    var showAddSheet by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Form fields state
    var newProductName by remember { mutableStateOf("") }
    var newProductDescription by remember { mutableStateOf("") }
    var newProductPrice by remember { mutableStateOf("") }
    var newProductStock by remember { mutableStateOf("") }

    // Logic for successful product addition/update
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showAddSheet = false
            editingProduct = null
            viewModel.resetSuccessState()
            Toast.makeText(context, "Produk berhasil disimpan!", Toast.LENGTH_SHORT).show()
        }
    }

    // Logic for showing errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    // Pre-fill form when editing
    LaunchedEffect(editingProduct) {
        editingProduct?.let {
            newProductName = it.name
            newProductDescription = it.description
            newProductPrice = it.price.toString()
            newProductStock = it.stock.toString()
        }
    }

    // Reset search query when search bar is hidden
    LaunchedEffect(isSearchVisible) {
        if (!isSearchVisible) {
            searchQuery = ""
        }
    }

    // Reset form fields when sheet is closed
    LaunchedEffect(showAddSheet) {
        if (!showAddSheet) {
            if (editingProduct == null) {
                newProductName = ""
                newProductDescription = ""
                newProductPrice = ""
                newProductStock = ""
            }
        }
    }
    
    val filteredProducts = remember(searchQuery, uiState.productList) {
        if (searchQuery.isEmpty()) {
            uiState.productList
        } else {
            uiState.productList.filter { it.name.startsWith(searchQuery, ignoreCase = true) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGray)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { 
                if (isSearchVisible) isSearchVisible = false 
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Produk Saya",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = InterFamily,
                    color = Color.Black
                )
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.Gray)
                        ) { isSearchVisible = !isSearchVisible },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Toggle Search",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Animated Search Bar
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(modifier = Modifier.clickable(enabled = false) { }) { // Menahan agar klik di search bar tidak men-close
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(56.dp),
                            placeholder = { 
                                Text(
                                    "Cari produk...", 
                                    color = textColorSecondary.copy(alpha = 0.6f),
                                    fontFamily = InterFamily
                                ) 
                            },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Search, 
                                    contentDescription = null, 
                                    tint = textColorSecondary.copy(alpha = 0.6f)
                                ) 
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = brandGreen.copy(alpha = 0.3f),
                                unfocusedBorderColor = Color(0xFFE2EBE5),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = brandGreen,
                                selectionColors = TextSelectionColors(
                                    handleColor = brandGreen,
                                    backgroundColor = brandGreen.copy(alpha = 0.4f)
                                )
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Section Label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAFTAR PRODUK",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFamily,
                    color = textColorSecondary,
                    letterSpacing = 0.5.sp
                )
                
                Surface(
                    color = brandGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Total: ${filteredProducts.size} Produk",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandGreen,
                        fontFamily = InterFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product, 
                        brandGreen = brandGreen, 
                        textColorSecondary = textColorSecondary,
                        onEditClick = {
                            editingProduct = product
                            showAddSheet = true
                        },
                        onDeleteClick = {
                            productToDelete = product
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { 
                editingProduct = null
                showAddSheet = true 
            },
            containerColor = brandGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
        }

        // Modal Bottom Sheet for Add/Edit Product
        if (showAddSheet) {
            ModalBottomSheet(
                onDismissRequest = { 
                    showAddSheet = false
                    editingProduct = null
                },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 40.dp)
                ) {
                    Text(
                        text = if (editingProduct == null) "Tambah Produk Baru" else "Edit Produk",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    AddProductTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        label = "Nama Produk",
                        placeholder = "Masukkan nama produk",
                        brandGreen = brandGreen
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AddProductTextField(
                        value = newProductDescription,
                        onValueChange = { newProductDescription = it },
                        label = "Deskripsi",
                        placeholder = "Masukkan deskripsi singkat",
                        brandGreen = brandGreen
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AddProductTextField(
                            value = newProductPrice,
                            onValueChange = { if (it.all { char -> char.isDigit() }) newProductPrice = it },
                            label = "Harga",
                            placeholder = "0",
                            brandGreen = brandGreen,
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = Color.Black) }
                        )
                        AddProductTextField(
                            value = newProductStock,
                            onValueChange = { if (it.all { char -> char.isDigit() }) newProductStock = it },
                            label = "Stok",
                            placeholder = "0",
                            brandGreen = brandGreen,
                            modifier = Modifier.weight(0.7f),
                            keyboardType = KeyboardType.Number
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { 
                            if (newProductName.isNotBlank() && newProductPrice.isNotBlank() && newProductStock.isNotBlank()) {
                                if (editingProduct == null) {
                                    viewModel.addProduct(
                                        name = newProductName,
                                        description = newProductDescription,
                                        price = newProductPrice.toIntOrNull() ?: 0,
                                        stock = newProductStock.toIntOrNull() ?: 0
                                    )
                                } else {
                                    viewModel.updateProduct(
                                        id = editingProduct!!.id,
                                        name = newProductName,
                                        description = newProductDescription,
                                        price = newProductPrice.toIntOrNull() ?: 0,
                                        stock = newProductStock.toIntOrNull() ?: 0
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brandGreen,
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (editingProduct == null) "Simpan Produk" else "Simpan Perubahan", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold,
                                fontFamily = InterFamily,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && productToDelete != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    productToDelete = null
                },
                confirmButton = {
                    val interactionSource = remember { MutableInteractionSource() }
                    Text(
                        text = "Hapus",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(color = Color.Red.copy(alpha = 0.1f))
                            ) {
                                viewModel.deleteProduct(productToDelete!!.id)
                                showDeleteDialog = false
                                productToDelete = null
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                },
                dismissButton = {
                    val interactionSource = remember { MutableInteractionSource() }
                    Text(
                        text = "Batal",
                        color = textColorSecondary,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFamily,
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(color = Color.Gray)
                            ) { 
                                showDeleteDialog = false
                                productToDelete = null
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                },
                title = { Text("Hapus Produk", fontWeight = FontWeight.Bold, color = Color.Black) },
                text = { Text("Apakah Anda yakin ingin menghapus produk \"${productToDelete!!.name}\"?", color = Color.Black) },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun AddProductTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    brandGreen: Color,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            fontFamily = InterFamily,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f), fontSize = 14.sp) },
            prefix = prefix,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = brandGreen,
                focusedBorderColor = brandGreen,
                unfocusedBorderColor = Color(0xFFE2EBE5),
                selectionColors = TextSelectionColors(
                    handleColor = brandGreen,
                    backgroundColor = brandGreen.copy(alpha = 0.4f)
                )
            ),
            singleLine = true
        )
    }
}

@Composable
fun ProductCard(
    product: ProductItem,
    brandGreen: Color,
    textColorSecondary: Color,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.Gray)
            ) { /* Action Detail Produk */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.description,
                        fontSize = 12.sp,
                        color = textColorSecondary,
                        fontFamily = InterFamily
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF2F2))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.Red.copy(alpha = 0.1f))
                            ) { onDeleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.Gray)
                        ) { onEditClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = textColorSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rp ${String.format(Locale("in", "ID"), "%,d", product.price).replace(",", ".")}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = brandGreen,
                    fontFamily = InterFamily
                )
                
                Surface(
                    color = brandGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Stok: ${product.stock}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandGreen,
                        fontFamily = InterFamily
                    )
                }
            }
        }
    }
}
