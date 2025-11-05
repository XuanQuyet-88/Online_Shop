package com.example.onlineshop.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
    fun loadFiltered(id: String): MutableState<List<ItemsModel>> = repo.loadFiltered(id)

    val selectedItem: MutableState<ItemsModel?> = mutableStateOf(null)

    fun getItemById(itemId: String) {
        repo.loadItemById(itemId) { item ->
            selectedItem.value = item
        }
    }
}