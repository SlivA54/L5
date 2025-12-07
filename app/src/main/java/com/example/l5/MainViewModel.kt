package com.example.l5

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>
    val searchResults: MutableLiveData<List<Product>>

    init {
        val productDao = ProductRoomDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.allProducts
        searchResults = repository.searchResults
    }

    fun insertProduct(product: Product) = repository.insertProduct(product)
    fun findProduct(name: String) = repository.findProduct(name)
    fun deleteProduct(name: String) = repository.deleteProduct(name)
}
