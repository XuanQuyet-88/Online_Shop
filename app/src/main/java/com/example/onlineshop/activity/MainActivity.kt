package com.example.onlineshop.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.onlineshop.navigation.OnlineShopApp
import com.example.onlineshop.ui.theme.OnlineShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlineShopTheme {
                Log.d("ccc", "MainActivity: onCreate")
                OnlineShopApp()
            }
        }
    }
}