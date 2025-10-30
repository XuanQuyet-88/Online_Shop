package com.example.onlineshop.viewModel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.example.onlineshop.model.CategoryModel
import com.example.onlineshop.model.ItemsModel
import com.example.onlineshop.model.SliderModel
import com.example.onlineshop.repository.MainRepository

class MainViewModel(): ViewModel() {
    private val repo = MainRepository()
    fun loadBanner() : MutableState<List<SliderModel>> = repo.loadBanner()
    fun loadCategory(): MutableState<List<CategoryModel>> = repo.loadCategory()
    fun loadPopular(): MutableState<List<ItemsModel>> = repo.loadPopular()
}