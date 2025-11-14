package com.example.onlineshop.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val repo = AuthRepository()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()
    private val _username = MutableStateFlow<String>("")
    val username = _username.asStateFlow()


    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repo.registerUser(email, password, name)
            _authState.value = if (result.isSuccess) AuthState.Success else AuthState.Error(
                result.exceptionOrNull()?.message ?: "Unknown error"
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repo.loginUser(email, password)
            if (result.isSuccess) {
                Log.d("ccc", "AuthViewModel: Success")
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }


    fun logout() = repo.logout()

    fun getUserName(){
        viewModelScope.launch {
            val result = repo.getUserName()
            if(result.isSuccess){
                _username.value = result.getOrNull() ?: ""
                Log.d("ccc", "User name loaded: ${_username.value}")
            }else{
                _username.value = ""
            }
        }
    }

    fun resetPassword(email: String){
        viewModelScope.launch {
            try {
                repo.resetPassword(email)
            }catch (e: Exception){
                Log.d("ResetPassword", "Error: ${e.message}")
            }
        }
    }
}