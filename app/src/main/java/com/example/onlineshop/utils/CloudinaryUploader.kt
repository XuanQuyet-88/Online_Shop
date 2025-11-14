// CloudinaryUploader.kt (Updated with better error handling)
package com.example.onlineshop.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

// Cấu hình Cloudinary - Thay thế bằng thông tin thực của bạn
const val CLOUDINARY_CLOUD_NAME = "dmevkycje"
const val UPLOAD_PRESET = "avt_online_shop_app"

suspend fun uploadImageToCloudinary(
    context: Context,
    imageUri: Uri,
    dbRef: DatabaseReference,
    uid: String
): Pair<Boolean, String?> {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                return@withContext Pair(false, "Failed to read image file")
            }
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val requestBody = MultipartBody.Part.createFormData(
                "file", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val uploadPresetPart = MultipartBody.Part.createFormData("upload_preset", UPLOAD_PRESET)

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload")
                .post(
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(requestBody)
                        .addPart(uploadPresetPart)
                        .build()
                )
                .build()

            val response = client.newCall(request).execute()
            val responseCode = response.code
            val responseMessage = response.message
            val responseBody = response.body?.string()

            response.close()

            if (response.isSuccessful) {
                try {
                    val json = JSONObject(responseBody)
                    val secureUrl = json.optString("secure_url", null)
                    if (secureUrl.isNotEmpty()) {
                        dbRef.child("avtUrl").setValue(secureUrl)
                        file.delete()
                        return@withContext Pair(true, secureUrl)
                    } else {
                        return@withContext Pair(false, "Invalid response from Cloudinary")
                    }
                } catch (jsonEx: Exception) {
                    return@withContext Pair(false, "Invalid response format from Cloudinary")
                }
            } else {
                file.delete()
                return@withContext Pair(false, "Upload failed with code $responseCode: $responseMessage")
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "An unexpected error occurred during upload"
            return@withContext Pair(false, errorMsg)
        }
    }
}