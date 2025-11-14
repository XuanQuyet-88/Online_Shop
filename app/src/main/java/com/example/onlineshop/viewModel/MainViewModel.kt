package com.example.onlineshop.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.model.CategoryModel
import com.example.onlineshop.model.ItemsModel
import com.example.onlineshop.model.SliderModel
import com.example.onlineshop.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {
    private val _searchResults = MutableStateFlow<List<ItemsModel>>(emptyList())
    val searchResults: StateFlow<List<ItemsModel>> = _searchResults
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    private var allItems: List<ItemsModel> = emptyList()

    private val repo = MainRepository()
    fun loadBanner(): MutableState<List<SliderModel>> = repo.loadBanner()
    fun loadCategory(): MutableState<List<CategoryModel>> = repo.loadCategory()
    fun loadPopular(): MutableState<List<ItemsModel>> = repo.loadPopular()
    fun loadFiltered(id: String): MutableState<List<ItemsModel>> = repo.loadFiltered(id)

    fun loadSearchByTitle(query: String): MutableState<List<ItemsModel>> =
        repo.loadSearchByTitle(query)

    val selectedItem: MutableState<ItemsModel?> = mutableStateOf(null)
    fun getItemById(itemId: String) {
        repo.loadItemById(itemId) { item ->
            selectedItem.value = item
        }
    }

    init {
        viewModelScope.launch {
            repo.loadAllItem { items ->
                allItems = items
            }
        }
    }
}