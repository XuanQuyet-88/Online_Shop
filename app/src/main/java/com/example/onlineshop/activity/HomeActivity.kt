package com.example.onlineshop.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.onlineshop.R
import com.example.onlineshop.model.CategoryModel
import com.example.onlineshop.model.SliderModel
import com.example.onlineshop.viewModel.AuthViewModel
import com.example.onlineshop.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun MainActivityScreen(
    navController: androidx.navigation.NavController,
    onCartClick: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val username = authViewModel.username.collectAsState().value

    if (currentUser != null) {
        Log.d("ccc", "Đã đăng nhập: ${currentUser.email}")
    } else {
        Log.d("ccc", "Chưa đăng nhập")
    }

    val viewModel = MainViewModel()
    val bannerState = viewModel.loadBanner()
    val categoriesState = viewModel.loadCategory()
    val popularState = viewModel.loadPopular()
    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember { mutableStateOf(true) }
    var showPopularLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        authViewModel.getUserName()
    }

    LaunchedEffect(Unit) {
        viewModel.loadBanner()
        showBannerLoading = false
    }

    LaunchedEffect(Unit) {
        viewModel.loadCategory()
        showCategoryLoading = false
    }

    LaunchedEffect(Unit) {
        viewModel.loadPopular()
        showPopularLoading = false
    }

    ConstraintLayout(modifier = Modifier.background(Color.White)) {
        val (scrollList, bottomMenu) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Welcome back", color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = username.toString(),
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Image(
                            painter = painterResource(R.drawable.bell_icon),
                            contentDescription = null
                        )
                    }
                }
            }

            // Banner với loading
            item {
                if (showBannerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Banner(bannerState.value)
                }
            }

            // Categories với navController
            item {
                if (showCategoryLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    CategoryList(
                        categories = categoriesState.value,
                        navController = navController
                    )
                }
            }

            // Popular với loading
            item {
                if (showPopularLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    SectionTitle(title = "Popular Items", actionText = "See all")
                    ListItems(popularState.value)
                }
            }
        }

        // Bottom Menu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(8.dp, RoundedCornerShape(30.dp))
                .background(colorResource(R.color.darkBrown))
                .constrainAs(bottomMenu) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
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
                    onItemClick = { /* Home logic */ }
                )
                BottomMenuItem(
                    icon = painterResource(R.drawable.btn_2),
                    text = "Cart",
                    onItemClick = onCartClick
                )
                BottomMenuItem(
                    icon = painterResource(R.drawable.btn_3),
                    text = "Favorite",
                    onItemClick = { /* Favorite */ }
                )
                BottomMenuItem(
                    icon = painterResource(R.drawable.btn_4),
                    text = "Orders",
                    onItemClick = { /* Orders */ }
                )
                BottomMenuItem(
                    icon = painterResource(R.drawable.btn_5),
                    text = "Profile",
                    onItemClick = { /* Profile */ }
                )
            }
        }
    }
}

@Composable
fun BottomMenuItem(
    icon: Painter,
    text: String,
    onItemClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .height(70.dp)
            .clickable { onItemClick?.invoke() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = text,
            tint = Color.White
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp
        )
    }
}

@Composable
fun CategoryList(
    categories: List<CategoryModel>,
    navController: androidx.navigation.NavController
) {
    var selectedIndex by remember { mutableStateOf(-1) }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(
                item = categories[index],
                isSelected = selectedIndex == index,
                onItemClick = {
                    selectedIndex = index
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate("list_items/${categories[index].id}/${categories[index].title}")
                    }, 500)
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    item: CategoryModel,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = item.picUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(if (isSelected) 60.dp else 50.dp)
                .background(
                    color = if (isSelected) colorResource(R.color.darkBrown) else colorResource(R.color.lightBrown),
                    shape = RoundedCornerShape(100.dp)
                ),
            contentScale = ContentScale.Inside,
            colorFilter = if (isSelected) ColorFilter.tint(Color.White) else ColorFilter.tint(Color.Black)
        )

        Spacer(modifier = Modifier.padding(top = 8.dp))

        Text(
            text = item.title,
            color = colorResource(R.color.darkBrown),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionTitle(title: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = actionText,
            color = colorResource(R.color.darkBrown)
        )
    }
}

@Composable
fun Banner(banners: List<SliderModel>) {
    AutoSliding(banners = banners)
}

@Composable
fun AutoSliding(
    modifier: Modifier = Modifier,
    banners: List<SliderModel>
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    if (banners.isNotEmpty()) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                if (!isDragged && banners.isNotEmpty()) {
                    val nextPage = (pagerState.currentPage + 1) % banners.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].url)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp)
                    .height(150.dp)
            )
        }
        DotIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = if (isDragged) pagerState.currentPage else pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun DotIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = colorResource(R.color.darkBrown),
    unSelectedColor: Color = colorResource(R.color.grey),
    dotSize: Dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )
            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}