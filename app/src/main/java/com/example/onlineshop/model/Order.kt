package com.example.onlineshop.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending",
    val totalPrice: Double = 0.0,
    val address: String = "",
    val phone: String = "",
    val paymentMethod: String = "cash",
    val items: Map<String, CartItem> = emptyMap()
){
    fun formattedDate(): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
