package com.example.onlineshop.model

import android.os.Parcel
import android.os.Parcelable

data class CartItem (
    var productId: String = "",
    var model: String = "",
    var quantity: Int = 0,
    var price: Double = 0.0,
    var imageUrl: String = "",
    var title: String = ""
)