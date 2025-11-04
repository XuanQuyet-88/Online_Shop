package com.example.onlineshop.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.onlineshop.R
import com.example.onlineshop.helper.CartManager
import com.example.onlineshop.model.ItemsModel
import com.google.firebase.auth.FirebaseAuth

class DetailActivity : BaseActivity() {
    private lateinit var item: ItemsModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = intent.getSerializableExtra("object") as ItemsModel

        setContent {
            var selectedModelIndex by remember { mutableStateOf(-1) }
            DetailScreen(
                item = item,
                onBackClick = { finish() },
                selectedModelIndex = selectedModelIndex,
                onModelSelected = { selectedModelIndex = it },
                onAddToCartClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown User"
                    val selectedModel =
                        if (selectedModelIndex >= 0) item.model[selectedModelIndex] else ""
                    if (selectedModel.isEmpty()) {
                        Toast.makeText(this, "Please select a model first", Toast.LENGTH_SHORT)
                            .show()
                        return@DetailScreen
                    }
                    Log.d("ccc", "id product: ${item.id}")
                    CartManager.addToCart(
                        userId,
                        item.id,
                        selectedModel,
                        item.price,
                        1,
                        item.picUrl.first(),
                        item.title
                    )
                    Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                },
                onCartClick = {
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }

    @Composable
    private fun DetailScreen(
        item: ItemsModel,
        selectedModelIndex: Int,
        onModelSelected: (Int) -> Unit,
        onBackClick: () -> Unit,
        onAddToCartClick: () -> Unit,
        onCartClick: () -> Unit
    ) {
        var selectedImageUrl by remember { mutableStateOf(item.picUrl.first()) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .padding(bottom = 16.dp)
            ) {
                val (back, fav, mainImage, thumbnail) = createRefs()
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            colorResource(R.color.lightBrown),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .constrainAs(mainImage) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                )

                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 48.dp, start = 8.dp)
                        .clickable { onBackClick() }
                        .constrainAs(back) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )

                LazyRow(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .background(
                            color = colorResource(R.color.white),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .constrainAs(thumbnail) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    items(item.picUrl) { imageUrl ->
                        ImageThumbnail(
                            imageUrl = imageUrl,
                            isSelected = imageUrl == selectedImageUrl,
                            onBackClick = { selectedImageUrl = imageUrl }
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(end = 16.dp)
                )
                Text(
                    text = "$${item.price}",
                    fontSize = 22.sp
                )
            }
            RatingBar(rating = item.rating)
            ModelDelector(
                models = item.model,
                selectedModelIndex = selectedModelIndex,
                onModelSelected = onModelSelected
            )
            Text(
                text = item.description,
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.background(
                        colorResource(R.color.lightBrown),
                        shape = RoundedCornerShape(10.dp)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.btn_2),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                Button(
                    onClick = onAddToCartClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.darkBrown)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Add to cart", color = Color.White)
                }
            }
        }
    }

    @Composable
    fun ModelDelector(
        models: ArrayList<String>,
        selectedModelIndex: Int,
        onModelSelected: (Int) -> Unit
    ) {
        LazyRow(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            itemsIndexed(models) { index, model ->
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .height(40.dp)
                        .then(
                            if (index == selectedModelIndex) {
                                Modifier.border(
                                    1.dp, colorResource(R.color.darkBrown),
                                    RoundedCornerShape(10.dp)
                                )
                            } else {
                                Modifier.border(
                                    1.dp, colorResource(R.color.darkBrown),
                                    RoundedCornerShape(10.dp)
                                )
                            }
                        )
                        .background(
                            if (index == selectedModelIndex) colorResource(R.color.darkBrown)
                            else colorResource(R.color.veryLightBrown),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onModelSelected(index) }
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = model,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (index == selectedModelIndex) Color.White else Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    @Composable
    fun RatingBar(rating: Double) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Select Model",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(R.drawable.star),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "$rating Rating", style = MaterialTheme.typography.bodyMedium)
        }
    }

    @Composable
    fun ImageThumbnail(imageUrl: String, isSelected: Boolean, onBackClick: () -> Unit) {
        val backColor =
            if (isSelected) colorResource(R.color.darkBrown) else colorResource(R.color.veryLightBrown)

        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(55.dp)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            1.dp,
                            colorResource(R.color.darkBrown),
                            RoundedCornerShape(10.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .background(backColor, shape = RoundedCornerShape(10.dp))
                .clickable(onClick = onBackClick)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}