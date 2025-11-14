package com.example.onlineshop.data.repository

import android.util.Log
import com.example.onlineshop.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("Users")

    suspend fun registerUser(email: String, password: String, name: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(kotlin.Exception("No UID"))
            val user = UserModel(uid, email, name)
            db.child(uid).setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d("ccc", "Login success: ${user.email}")
                Result.success(Unit)
            } else {
                Log.d("ccc", "Login failed: user is null")
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Log.e("ccc", "Login error", e)
            Result.failure(e)
        }
    }


    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    suspend fun getUserName(): Result<String> {
        return try {
            val uid =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val snapshot = db.child(uid).get().await()
            if (snapshot.exists()) {
                val name = snapshot.child("name").getValue(String::class.java)
                if (name != null) {
                    Result.success(name)
                } else {
                    Result.failure(Exception("Name not found"))
                }
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d("ResetPassword", "Email sent")
                }else{
                    Log.d("ResetPassword", "Email not sent")
                }
            }
    }
}