package com.example.onlineshop.repository

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import com.example.onlineshop.model.CartItem
import com.example.onlineshop.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class OrderRepository {
    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _orders = mutableStateOf<List<Order>>(emptyList())
    val orders: State<List<Order>> = _orders

    fun loadOrders(onLoaded: (List<Order>) -> Unit): State<List<Order>> {
        val userId = auth.currentUser?.uid ?: return mutableStateOf(emptyList())
        val ref = db.getReference("Orders").child(userId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = mutableListOf<Order>()
                for (orderSnap in snapshot.children) {
                    val orderMap = orderSnap.value as? Map<*, *>
                    if (orderMap != null) {
                        val orderId = orderMap["orderId"] as? String ?: continue
                        val itemsMap = orderMap["items"] as? Map<*, *> ?: continue
//                        val itemsList = mutableListOf<CartItem>()
                        val items = mutableMapOf<String, CartItem>()
                        itemsMap.forEach { (_, itemMap) ->
                            val itemData = itemMap as? Map<*, *>
                            if (itemData != null) {
                                val item = CartItem(
                                    productId = itemData["productId"] as? String ?: "",
                                    model = itemData["model"] as? String ?: "",
                                    price = (itemData["price"] as? Number)?.toDouble() ?: 0.0,
                                    quantity = (itemData["quantity"] as? Number)?.toInt() ?: 0,
                                    imageUrl = itemData["imageUrl"] as? String ?: "",
                                    title = itemData["title"] as? String ?: ""
                                )
                                items[item.productId] = item
                            }
                        }
                        val order = Order(
                            orderId = orderId,
                            userId = userId,
                            timestamp = (orderMap["timestamp"] as? Number)?.toLong() ?: 0L,
                            status = orderMap["status"] as? String ?: "Pending",
                            totalPrice = (orderMap["totalPrice"] as? Number)?.toDouble() ?: 0.0,
                            address = orderMap["address"] as? String ?: "",
                            phone = orderMap["phone"] as? String ?: "",
                            paymentMethod = orderMap["paymentMethod"] as? String ?: "cash",
                            items = items
                        )
                        orderList.add(order)
                    }
                }
                _orders.value = orderList.sortedByDescending { it.timestamp }
                onLoaded(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderRepository", "loadOrders:onCancelled", error.toException())
                onLoaded(emptyList())
            }
        })
        return orders
    }

    suspend fun cancelOrder(orderId: String): Boolean = suspendCancellableCoroutine { continuation ->
        val userId = auth.currentUser?.uid ?: run {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }
        val orderRef = db.getReference("Orders").child(userId).child(orderId)
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderMap = snapshot.value as? Map<*, *>
                val timestamp = (orderMap?.get("timestamp") as? Number)?.toLong() ?: 0L
                val currentTime = System.currentTimeMillis()
                val timeDk = currentTime - timestamp
                if (timeDk > 86_400_000L) {
                    continuation.resume(false)
                    return
                }

                orderRef.child("status").setValue("Cancelled").addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("OrderRepository", "Order $orderId cancelled successfully")
                        continuation.resume(true)
                    } else {
                        Log.e("OrderRepository", "Cancel order failed: ${task.exception?.message}")
                        continuation.resume(false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(false)
            }
        })
    }

    suspend fun createOrder(
        cartItems: List<CartItem>,
        totalPrice: Double,
        address: String,
        phone: String,
        paymentMethod: String
    ): String? = suspendCancellableCoroutine { continuation ->
        val userId = auth.currentUser?.uid ?: run {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        val orderRef = db.getReference("Orders").child(userId)
        val orderId = orderRef.push().key ?: run {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val itemsMap = mutableMapOf<String, Any>()
        cartItems.forEachIndexed { index, item ->
            val itemKey = orderRef.child(orderId).child("items").push().key ?: "item$index"
            itemsMap[itemKey] = mapOf(
                "productId" to item.productId,
                "model" to item.model,
                "price" to item.price,
                "quantity" to item.quantity,
                "imageUrl" to item.imageUrl,
                "title" to item.title
            )
        }

        val orderData = mapOf(
            "orderId" to orderId,
            "userId" to userId,
            "timestamp" to System.currentTimeMillis(),
            "status" to "Pending",
            "totalPrice" to totalPrice,
            "address" to address,
            "phone" to phone,
            "paymentMethod" to paymentMethod,
            "items" to itemsMap
        )
        orderRef.child(orderId).setValue(orderData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(orderId)
            } else {
                continuation.resume(null)
            }
        }
    }
}