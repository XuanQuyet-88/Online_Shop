package com.example.onlineshop.activity

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.RadioButton
import coil.compose.rememberAsyncImagePainter
import com.example.onlineshop.R
import com.example.onlineshop.helper.CartManager
import com.example.onlineshop.model.CartItem
import com.example.onlineshop.viewModel.CheckoutViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    checkoutViewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        onBackClick()
        return
    }

    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val singleItem = checkoutViewModel.singleItem.value
    val isSingleItem = singleItem != null
    val totalPrice = cartItems.sumOf { it.price * it.quantity }
    Log.d("Checkout", "$singleItem")

    LaunchedEffect(userId, singleItem) {
        Log.d("Checkout", "Bắt đầu load: ${if (isSingleItem) "Single item" else "Full cart"}")
        if (isSingleItem) {
            cartItems = listOf(singleItem)
            isLoading = false
        } else {
            CartManager.getCart(userId) { items ->
                cartItems = items
                isLoading = false
                Log.d("Checkout", "Loaded ${items.size} items từ cart")
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // check cart empty
    if (cartItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Your cart is empty", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("Back to Cart")
            }
        }
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Header
        item {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (backIcon, title) = createRefs()
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .constrainAs(backIcon) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )
                Text(
                    text = "Checkout",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(backIcon.end, margin = 16.dp)
                    }
                )
            }
        }

        val titleText = if (isSingleItem) "Buy ${cartItems.first().productId}" else "Order Summary"
        // Cart Items Summary
        item {
            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(cartItems) { item ->
            CheckoutItemRow(item = item)
        }

        // Delivery Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Delivery Address",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Payment Method
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Payment Method",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedPayment = "cash" }
                    ) {
                        RadioButton(
                            selected = selectedPayment == "cash",
                            onClick = { selectedPayment = "cash" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Money,
                                contentDescription = "Cash",
                                tint = colorResource(R.color.darkBrown)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cash on Delivery")
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedPayment = "card" }
                    ) {
                        RadioButton(
                            selected = selectedPayment == "card",
                            onClick = { selectedPayment = "card" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Card",
                                tint = colorResource(R.color.darkBrown)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Credit Card")
                        }
                    }
                }
            }
        }

        // Total price
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: $${"%.2f".format(totalPrice)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // Confirm Button
        item {
            Button(
                onClick = {
                    if (address.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please fill in address and phone",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (phone.length < 10) {
                        Toast.makeText(
                            context,
                            "Please enter a valid phone number",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if(!isSingleItem){
                        CartManager.clearCart(userId)
                    }
                    Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                    checkoutViewModel.clearSingleItem()
                    navController.navigate("orders") {
                        popUpTo("cart") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.darkBrown)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Confirm Order", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun CheckoutItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colorResource(R.color.lightBrown), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = item.imageUrl),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Model: ${item.model}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "SL: ${item.quantity} x $${item.price}",
                fontSize = 14.sp
            )
        }
        Text(
            text = "$${"%.2f".format(item.price * item.quantity)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}