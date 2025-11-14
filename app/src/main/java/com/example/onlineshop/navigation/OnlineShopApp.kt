package com.example.onlineshop.navigation

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.onlineshop.ui.screens.home.CartScreen
import com.example.onlineshop.ui.screens.home.CheckOutScreen
import com.example.onlineshop.ui.screens.home.DetailScreen
import com.example.onlineshop.ui.screens.home.ListItemScreen
import com.example.onlineshop.ui.screens.home.MainActivityScreen
import com.example.onlineshop.ui.screens.home.OrderScreen
import com.example.onlineshop.ui.screens.home.ProfileScreen
import com.example.onlineshop.ui.screens.home.SearchScreen
import com.example.onlineshop.ui.screens.auth.LoginScreen
import com.example.onlineshop.ui.screens.auth.RegisterScreen
import com.example.onlineshop.ui.screens.auth.ResetPasswordScreen
import com.example.onlineshop.viewModel.CheckoutViewModel
import com.example.onlineshop.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun OnlineShopApp() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) Routes.HOME else Routes.LOGIN
    val checkoutViewModel: CheckoutViewModel = viewModel()

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
                },
                navController = navController
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
                navController = navController,
                checkoutViewModel = checkoutViewModel
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

        composable(Routes.CHECK_OUT) {
            CheckOutScreen(
                onBackClick = { navController.popBackStack() },
                navController = navController,
                checkoutViewModel = checkoutViewModel
            )
        }
        composable(Routes.ORDERS) {
            OrderScreen(
                onBackClick = {navController.popBackStack()},
                navController = navController
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBackClick = {navController.popBackStack()},
                navController = navController
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                navController = navController
            )
        }
    }
}