package com.example.onlineshop.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlineshop.activity.MainActivityScreen
import com.example.onlineshop.screens.LoginScreen
import com.example.onlineshop.screens.RegisterScreen
import com.example.onlineshop.screens.ResetPasswordScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OnlineShopApp() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) Routes.HOME else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                    Log.d("ccc", "LoginScreen: success")
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onResetPasswordClick = {
                    navController.navigate(Routes.RESET_PASSWORD)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                        Log.d("ccc", "RegisterScreen: success")
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.HOME) {
            MainActivityScreen(
                onCartClick = {
                }
            )
        }

        composable(Routes.RESET_PASSWORD) {
            ResetPasswordScreen(
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
    }
}