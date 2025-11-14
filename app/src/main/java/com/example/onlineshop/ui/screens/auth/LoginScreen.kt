package com.example.onlineshop.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.onlineshop.R
import com.example.onlineshop.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onResetPasswordClick: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.authState.collectAsState()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordHidden = remember { mutableStateOf(true) }

    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = null,
        modifier = Modifier
            .statusBarsPadding()
            .background(Color.White)
            .fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "ONLINE SHOP",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(Modifier.height(16.dp))

        LottieLogin()

        Spacer(Modifier.height(8.dp))

        Text(
            "SIGN IN",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email icon",
                    tint = Color.Gray
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Gray
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password icon",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                val image = if (passwordHidden.value) {
                    Icons.Filled.VisibilityOff
                } else {
                    Icons.Filled.Visibility
                }

                IconButton(onClick = { passwordHidden.value = !passwordHidden.value }) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (passwordHidden.value) "Show password" else "Hide password",
                        tint = Color.Gray
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Gray
            ),
            visualTransformation = if (passwordHidden.value)
                PasswordVisualTransformation()
            else
                VisualTransformation.None
        )

        TextButton(onClick = {
            onResetPasswordClick()
        }) {
            Text(
                text = "Forgot password?",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                textAlign = TextAlign.End
            )
        }

        Button(
            onClick = { viewModel.login(email.value, password.value) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E88E5), // màu nền
                contentColor = Color.White          // màu chữ
            ),
            shape = RoundedCornerShape(16.dp),      // bo tròn
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                text = "Sign In",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account?",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = onRegisterClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }


        when (state) {
            is AuthViewModel.AuthState.Loading -> CircularProgressIndicator()
            is AuthViewModel.AuthState.Error -> Text(
                (state as AuthViewModel.AuthState.Error).message,
                color = Color.Red
            )

            is AuthViewModel.AuthState.Success -> {
                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }

            else -> {}
        }
        Log.d("ccc", "LoginScreen: ${state}")
    }
}

@Composable
fun LottieLogin() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp)
    )
}