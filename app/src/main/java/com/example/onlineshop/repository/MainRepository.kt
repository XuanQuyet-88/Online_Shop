package com.example.onlineshop.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.onlineshop.model.CategoryModel
import com.example.onlineshop.model.ItemsModel
import com.example.onlineshop.model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainRepository {
    private val TAG = "FirebaseRepo"

    private val db = FirebaseDatabase.getInstance()

    fun loadBanner(): MutableState<List<SliderModel>> {
        val listData = mutableStateOf<List<SliderModel>>(emptyList())
        val ref = db.getReference("Banner")

        Log.d(TAG, "loadBanner: Bắt đầu lắng nghe dữ liệu từ Firebase...")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                val lists = mutableListOf<SliderModel>()
//                for (i in snapshot.children) {
//                    val data = i.getValue(SliderModel::class.java)
//                    data?.let { lists.add(it) }
//                }
//                listData.value = lists

                if (snapshot.exists()) {
                    val lists = mutableListOf<SliderModel>()
                    for (i in snapshot.children) {
                        val data = i.getValue(SliderModel::class.java)
                        data?.let { lists.add(it) }
                    }
                    listData.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun loadCategory(): MutableState<List<CategoryModel>> {
        val listData = mutableStateOf<List<CategoryModel>>(emptyList())
        val ref = db.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val lists = mutableListOf<CategoryModel>()
                    for(i in snapshot.children){
                        val data = i.getValue(CategoryModel::class.java)
                        data?.let { lists.add(it) }
                    }
                    listData.value = lists
                    Log.d(TAG, "Số lượng category: ${listData.value}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun loadPopular(): MutableState<List<ItemsModel>>{
        val listData = mutableStateOf<List<ItemsModel>>(emptyList())
        val ref = db.getReference("Items")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val lists = mutableListOf<ItemsModel>()
                    for(i in snapshot.children){
                        val data = i.getValue(ItemsModel::class.java)
                        data?.let { lists.add(it) }
                    }
                    listData.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }
}