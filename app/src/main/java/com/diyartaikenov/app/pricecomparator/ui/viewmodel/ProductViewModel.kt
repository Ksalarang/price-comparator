package com.diyartaikenov.app.pricecomparator.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

import com.diyartaikenov.app.pricecomparator.data.ProductDao
import com.diyartaikenov.app.pricecomparator.model.Product

class ProductViewModel(productDao: ProductDao): ViewModel() {

    val products:LiveData<List<Product>> = productDao.getProducts().asLiveData()
}

class ProductViewModelFactory(
    private val productDao: ProductDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(productDao) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
