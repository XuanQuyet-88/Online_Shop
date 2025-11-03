package com.example.onlineshop.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshop.model.ItemsModel

class DetailActivity : AppCompatActivity() {
    private lateinit var item: ItemsModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        item = intent.getSerializableExtra("item") as ItemsModel

        setContent {
            DetailScreen(
                item = item,
                onBackClick = { finish()},
                onAddToCartClick = {
                    item.numberInCart = 1
                }
            )
        }
    }

    private fun DetailScreen(
        item: ItemsModel,
        onBackClick: () -> Unit,
        onAddToCartClick: () -> Unit
    ) {
    }
}