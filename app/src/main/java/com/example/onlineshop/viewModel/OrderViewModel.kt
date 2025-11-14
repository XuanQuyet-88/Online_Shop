package com.example.onlineshop.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.model.CartItem
import com.example.onlineshop.data.model.Order
import com.example.onlineshop.data.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val repo = OrderRepository()
    val orders: State<List<Order>> = repo.orders

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    var error: State<String?> = _error

    init {
        loadOrders()
    }

    private fun loadOrders() {
        _loading.value = true
        _error.value = null
        repo.loadOrders { orders ->
            _loading.value = false
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                val success = repo.cancelOrder(orderId)
                if (success) {
                    _error.value = null
                } else {
                    _error.value = "Failed to cancel order"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun createOrder(
        cartItems: List<CartItem>,
        totalPrice: Double,
        address: String,
        phone: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            try {
                val orderId = repo.createOrder(
                    cartItems = cartItems,
                    totalPrice = totalPrice,
                    address = address,
                    phone = phone,
                    paymentMethod = paymentMethod
                )
                if (orderId != null){
                    _error.value = null
                    loadOrders()
                }else{
                    _error.value = "Failed to create order"
                }
            }catch (e: Exception){
                _error.value = e.message
            }
        }
    }
}