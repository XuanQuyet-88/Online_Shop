package com.example.onlineshop.activity

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.onlineshop.R
import com.example.onlineshop.helper.CartManager
import com.example.onlineshop.model.CartItem
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CartScreen(onBackClick: () -> Unit, navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var refreshKey by remember { mutableStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect(refreshKey) {
        CartManager.getCart(userId) { items ->
            cartItems = items
        }
    }

    val totalPrice = cartItems.sumOf({ it.price * it.quantity })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp)
                    .size(46.dp)
                    .clickable { onBackClick() }
            )
            Text(
                text = "My Cart",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(end = 44.dp)
                    .weight(1f)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center

            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onIncreaseClick = {
                        CartManager.updateQuantity(
                            userId,
                            item.productId,
                            item.model,
                            item.quantity + 1
                        )
                        Log.d("ccc", "quantity product: ${item.quantity}")
                        refreshKey++
                    },
                    onDecreaseCLick = {
                        if (item.quantity > 1) {
                            CartManager.updateQuantity(
                                userId,
                                item.productId,
                                item.model,
                                item.quantity - 1
                            )
                            refreshKey++
                        } else {
                            Toast.makeText(
                                context,
                                "Tối thiểu 1 sản phẩm",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onDeleteClick = {
                        CartManager.removeFromCart(userId, item.productId, item.model)
                        refreshKey++
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: $${"%.2f".format(totalPrice)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)
            )
            Button(
                onClick = {
                    navController.navigate("check_out")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.darkBrown)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(end = 16.dp, bottom = 24.dp)
            ) {
                Text(text = "Checkout", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncreaseClick: () -> Unit,
    onDecreaseCLick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    var showDialogDelete by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colorResource(R.color.lightBrown), RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = item.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .background(Color.LightGray, RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productId, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Model: ${item.model}", fontSize = 14.sp, color = Color.Gray)
            Text("$${item.price}", fontSize = 16.sp, color = colorResource(R.color.darkBrown))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecreaseCLick) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease"
                )
            }
            Text(
                text = "${item.quantity}",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(onClick = onIncreaseClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase"
                )
            }
            IconButton(onClick = { showDialogDelete = true }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }

    if (showDialogDelete) {
        AlertDialog(
            onDismissRequest = { showDialogDelete = false },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick()
                    showDialogDelete = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {showDialogDelete = false}) {
                    Text("Cancel")
                }
            }
        )
    }
}