package com.example.onlineshop.activity

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlineshop.R
import com.example.onlineshop.model.Order
import com.example.onlineshop.navigation.Routes
import com.example.onlineshop.viewModel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OrderScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: OrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        onBackClick()
        return
    }

    val orders by viewModel.orders
    val loading by viewModel.loading
    val error by viewModel.error
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        error?.let { msg ->
            snackBarHostState.showSnackbar(msg)
            delay(3000)
//            viewModel.error.value = null
        }
    }

    val statusList = remember { listOf("All", "Pending", "Shipped", "Delivered", "Cancelled") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            val (header, listOrder, bottomMenu) = createRefs()

            // ----- HEADER -----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBackClick() }
                )

                Text(
                    text = "My Orders",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 44.dp),
                    textAlign = TextAlign.Center
                )
            }

            // ----- MAIN CONTENT -----
            Box(
                modifier = Modifier
                    .constrainAs(listOrder) {
                        top.linkTo(header.bottom, margin = 148.dp)
                        bottom.linkTo(bottomMenu.top, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                when {
                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    orders.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No orders yet", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { navController.navigate(Routes.HOME) }) {
                                Text("Go to shopping")
                            }
                        }
                    }

                    else -> {
                        val filteredOrders = remember(orders, selectedTabIndex) {
                            if (selectedTabIndex == 0) {
                                orders
                            } else {
                                orders.filter { it.status == statusList[selectedTabIndex] }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            ScrollableTabRow(
                                selectedTabIndex = selectedTabIndex,
                                modifier = Modifier.fillMaxWidth(),
                                edgePadding = 16.dp,
                                containerColor = TabRowDefaults.containerColor.copy(alpha = 0.1f),
                                contentColor = Color.Black,
                                indicator = { tabPositions: List<TabPosition> ->
                                    TabRowDefaults.Indicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = colorResource(R.color.orange)
                                    )
                                }
                            ) {
                                statusList.forEachIndexed { index, status ->
                                    Tab(
                                        selected = selectedTabIndex == index,
                                        onClick = {
                                            scope.launch {
                                                selectedTabIndex = index
                                            }
                                        },
                                        text = {
                                            val selectedColor =
                                                if (selectedTabIndex == index) colorResource(R.color.orange) else Color.Black
                                            Text(
                                                text = status,
                                                color = selectedColor
                                            )
                                        }
                                    )
                                }
                            }
                            if (filteredOrders.isEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "No orders for ${statusList[selectedTabIndex]} status",
                                        fontSize = 18.sp,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .fillMaxSize()
                                        .padding(bottom = 145.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(filteredOrders) { order ->
                                        OrderItemRow(
                                            order = order,
                                            onCancelClick = { orderId ->
                                                viewModel.cancelOrder(orderId)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ----- BOTTOM MENU -----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shadow(8.dp, RoundedCornerShape(30.dp))
                    .background(colorResource(R.color.darkBrown))
                    .constrainAs(bottomMenu) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(
                            colorResource(R.color.darkBrown),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_1),
                        text = "Home",
                        isSelected = false,
                        onItemClick = { navController.navigate(Routes.HOME) }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_2),
                        text = "Cart",
                        isSelected = false,
                        onItemClick = { navController.navigate(Routes.CART) }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_3),
                        text = "Favorite",
                        onItemClick = { /* TODO */ }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_4),
                        text = "Orders",
                        isSelected = true,
                        onItemClick = {
                            Toast.makeText(
                                context,
                                "You are in Orders Screen",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_5),
                        text = "Profile",
                        onItemClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(order: Order, onCancelClick: (String) -> Unit) {
    var showDialogCancel by remember { mutableStateOf(false) }
    val currentTime = System.currentTimeMillis()
    val timeDk = currentTime - order.timestamp
    val canCancel = timeDk < 86_400_000L && order.status == "Pending"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.formattedDate(),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                // Status chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (order.status) {
                        "Delivered" -> Color.Green
                        "Shipped" -> Color.Blue
                        "Cancelled" -> Color.Red
                        else -> Color.LightGray
                    },
                    contentColor = Color.White
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            //title item
            Text(
                text = "Items: ${
                    order.items.values.take(2).joinToString { it.title }
                } ${if (order.items.size > 2) "+${order.items.size - 2} more" else ""}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))

            //price, payment method
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total:", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "$${"%.2f".format(order.totalPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.darkBrown)
                    )
                }
                Text(
                    "Payment: ${order.paymentMethod}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            //adress
            Text(
                text = "Address: ${order.address}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            //cancel button
            if (canCancel) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        showDialogCancel = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text("Cancel Order", color = Color.White, fontSize = 14.sp)
                }
            } else if (order.status == "Pending") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cannot cancel after 24 hours",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }

            if (showDialogCancel) {
                AlertDialog(
                    onDismissRequest = { showDialogCancel = false },
                    title = { Text("Cancel Order") },
                    text = { Text("Are you sure you want to cancel this order?") },
                    confirmButton = {
                        TextButton(onClick = {
                            onCancelClick(order.orderId)
                            showDialogCancel = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialogCancel = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}