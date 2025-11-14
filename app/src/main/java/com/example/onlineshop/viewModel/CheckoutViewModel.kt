package com.example.onlineshop.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.onlineshop.data.model.CartItem

class CheckoutViewModel : ViewModel() {
    val singleItem: MutableState<CartItem?> = mutableStateOf(null)

    fun setSingleItem(item: CartItem) {
        singleItem.value = item
    }
    fun clearSingleItem() {
        singleItem.value = null
    }

    fun getSingleItem(): CartItem{
        return singleItem.value!!
    }
}