package com.example.onlineshop.helper

import com.example.onlineshop.data.model.CartItem
import com.google.firebase.database.FirebaseDatabase

object CartManager {
    private val userCarts = mutableMapOf<String, MutableList<CartItem>>()
    private val db = FirebaseDatabase.getInstance().getReference("carts")

    private fun getUserCart(userId: String): MutableList<CartItem> {
        return userCarts.getOrPut(userId) { mutableListOf() }
    }

    fun addToCart(
        userId: String,
        productId: String,
        model: String,
        price: Double,
        quantity: Int = 1,
        imageUrl: String = "",
        title: String = ""
    ) {
        val userRef = db.child(userId).child("$productId-$model")
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val currentQty = snapshot.child("quantity").getValue(Int::class.java) ?: 0
                val newQty = currentQty + quantity
                if (newQty > 0) {
                    userRef.updateChildren(mapOf("quantity" to newQty))
                } else {
                    userRef.removeValue()
                }
            } else {
                if (quantity > 0) {
                    val cartItem = mapOf(
                        "productId" to productId,
                        "model" to model,
                        "quantity" to quantity,
                        "price" to price,
                        "imageUrl" to imageUrl,
                        "title" to title
                    )
                    userRef.setValue(cartItem)
                }
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

    fun removeFromCart(userId: String, productId: String, model: String) {
        getUserCart(userId).removeIf { it.productId == productId }
        db.child(userId).child("$productId-$model").removeValue()
    }

    fun updateQuantity(userId: String, productId: String, model: String, newQty: Int) {
        db.child(userId).child("$productId-$model").child("quantity").setValue(newQty)
    }

    fun getCartItems(userId: String): List<CartItem> = getUserCart(userId)

    fun getTotalPrice(userId: String): Double {
        return getUserCart(userId).sumOf { it.price * it.quantity }
    }

    fun getCart(userId: String, onResult: (List<CartItem>) -> Unit){
        db.child(userId).get().addOnSuccessListener { snapshot ->
            val items = snapshot.children.mapNotNull {
                val productId = it.child("productId").getValue(String::class.java) ?: return@mapNotNull null
                val model = it.child("model").getValue(String::class.java) ?: ""
                val price = it.child("price").getValue(Double::class.java) ?: 0.0
                val quantity = it.child("quantity").getValue(Int::class.java) ?: 1
                val imageUrl = it.child("imageUrl").getValue(String::class.java) ?: ""
                val title = it.child("title").getValue(String::class.java) ?: ""
                CartItem(productId, model, quantity, price, imageUrl, title)
            }
            userCarts[userId] = items.toMutableList()
            onResult(items)
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

    fun clearCart(userId: String) {
        getUserCart(userId).clear()
        db.child(userId).removeValue()
    }
}