package com.example.l5

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class ProductRepository(private val productDao: ProductDao) {
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()
    val searchResults = MutableLiveData<List<Product>>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertProduct(newProduct: Product) =
        coroutineScope.launch(Dispatchers.IO) {
            productDao.insertProduct(newProduct)
        }

    fun deleteProduct(name: String) =
        coroutineScope.launch(Dispatchers.IO) {
            productDao.deleteProduct(name)
        }

    fun findProduct(name: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = withContext(Dispatchers.IO) {
                productDao.findProduct(name)
            }
        }
    }
}
