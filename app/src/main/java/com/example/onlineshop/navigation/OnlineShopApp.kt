package com.example.onlineshop.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.onlineshop.activity.CartScreen
import com.example.onlineshop.activity.ListItemScreen
import com.example.onlineshop.activity.MainActivityScreen
import com.example.onlineshop.screens.DetailScreen
import com.example.onlineshop.screens.LoginScreen
import com.example.onlineshop.screens.RegisterScreen
import com.example.onlineshop.screens.ResetPasswordScreen
import com.example.onlineshop.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("ViewModelConstructorInComposable")
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
                navController = navController,
                onCartClick = {
                    navController.navigate(Routes.CART)
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
        composable(Routes.CART) {
            CartScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            Routes.DETAIL,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailScreen(
                itemId = itemId,
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(
            Routes.LIST_ITEMS,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val title = backStackEntry.arguments?.getString("title") ?: ""
            ListItemScreen(
                title = title,
                id = id.toString(),
                onBackClick = { navController.popBackStack() },
                navController = navController,
                viewModel = MainViewModel()
            )
        }
    }
}