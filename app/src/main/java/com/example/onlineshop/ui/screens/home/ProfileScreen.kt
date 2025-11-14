package com.example.onlineshop.ui.screens.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.onlineshop.R
import com.example.onlineshop.data.model.UserModel
import com.example.onlineshop.navigation.Routes
import com.example.onlineshop.helper.uploadImageToCloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.get

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser ?: run {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Please log in", Toast.LENGTH_SHORT).show()
            onBackClick()
        }
        return
    }

    val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.uid)

    var userModel by remember { mutableStateOf(UserModel()) }
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var tempName by remember { mutableStateOf("") }
    var tempAvatarUrl by remember { mutableStateOf("") }
    var editingField by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Launcher chọn ảnh từ gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            if (editingField == "avatar") {
                isUploading = true
                coroutineScope.launch {
                    val result = uploadImageToCloudinary(
                        context = context,
                        imageUri = selectedUri,
                        dbRef = dbRef,
                        uid = currentUser.uid
                    )
                    val (success, data) = result
                    withContext(Dispatchers.Main) {
                        isUploading = false
                        if (success && data != null) {
                            tempAvatarUrl = data
                            userModel = userModel.copy(avtUrl = data)
                            Toast.makeText(context, "Avatar updated successfully", Toast.LENGTH_SHORT).show()
                            editingField = ""
                            isEditing = false
                        } else {
                            val error = data ?: "Unknown error"
                            Toast.makeText(context, "Upload failed: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(currentUser.uid) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMap = snapshot.value as? Map<*, *>
                if (userMap != null) {
                    val updatedUser = UserModel(
                        uid = currentUser.uid,
                        email = userMap["email"] as? String ?: "",
                        name = userMap["name"] as? String ?: "",
                        avtUrl = userMap["avtUrl"] as? String ?: ""
                    )
                    userModel = updatedUser
                    tempName = updatedUser.name
                    tempAvatarUrl = updatedUser.avtUrl
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Profile", "Load user data error: ${error.message}")
                isLoading = false
            }
        })
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            val (header, mainContent, bottomMenu) = createRefs()

            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBackClick() }
                )

                Text(
                    text = "My Profile",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 44.dp),
                    textAlign = TextAlign.Center
                )
            }

            //MAIN CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(mainContent) {
                        top.linkTo(header.bottom, margin = 148.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(bottomMenu.top, margin = 8.dp)
                    }
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val avtPainter = if (userModel.avtUrl.isEmpty()) {
                            painterResource(R.drawable.user_sign_in)
                        } else {
                            rememberAsyncImagePainter(model = userModel.avtUrl)
                        }
                        Image(
                            painter = avtPainter,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, colorResource(R.color.darkBrown), CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isEditing && editingField == "name") "" else (userModel.name.ifEmpty { "User" }),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (!isEditing) {
                                IconButton(onClick = {
                                    isEditing = true
                                    editingField = ""
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = colorResource(R.color.darkBrown)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = userModel.email.ifEmpty { "No email" },
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "ID: ${userModel.uid}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (isEditing) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Select to update",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editingField = "name" },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Name",
                                        tint = colorResource(R.color.darkBrown),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Sửa tên",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        editingField = "avatar"
                                        galleryLauncher.launch("image/*")
                                    },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.user_sign_in),
                                        contentDescription = "Edit Avatar",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = if (isUploading) "Uploading..." else "Sửa avatar",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            when (editingField) {
                                "name" -> {
                                    OutlinedTextField(
                                        value = tempName,
                                        onValueChange = { tempName = it },
                                        label = { Text("New name") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        TextButton(onClick = {
                                            editingField = ""
                                            tempName = userModel.name
                                        }) {
                                            Text("Cancel")
                                        }
                                        Button(
                                            onClick = {
                                                dbRef.child("name").setValue(tempName)
                                                Toast.makeText(
                                                    context,
                                                    "Your name is updated",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                isEditing = false
                                                editingField = ""
                                            },
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text("Save")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Routes.ORDERS) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.btn_2),
                                contentDescription = "Orders",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("My Orders", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    "View your order history",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "View",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                auth.signOut()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Logout",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Red
                                )
                                Text(
                                    "Sign out of your account",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "View",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            //BOTTOM MENU
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shadow(8.dp, RoundedCornerShape(30.dp))
                    .background(colorResource(R.color.darkBrown))
                    .constrainAs(bottomMenu) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(
                            colorResource(R.color.darkBrown),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_1),
                        text = "Home",
                        isSelected = false,
                        onItemClick = { navController.navigate(Routes.HOME) }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_2),
                        text = "Cart",
                        isSelected = false,
                        onItemClick = { navController.navigate(Routes.CART) }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_3),
                        text = "Favorite",
                        onItemClick = { /* TODO */ }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_4),
                        text = "Orders",
                        isSelected = false,
                        onItemClick = { navController.navigate(Routes.ORDERS) }
                    )
                    BottomMenuItem(
                        icon = painterResource(R.drawable.btn_5),
                        text = "Profile",
                        isSelected = true,
                        onItemClick = {
                            Toast.makeText(
                                context,
                                "You are in Profile Screen",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}