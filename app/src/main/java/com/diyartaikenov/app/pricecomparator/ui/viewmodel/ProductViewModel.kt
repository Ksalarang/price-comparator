package com.diyartaikenov.app.pricecomparator.ui.viewmodel

import androidx.lifecycle.*

import com.diyartaikenov.app.pricecomparator.data.ProductDao
import com.diyartaikenov.app.pricecomparator.model.FoodGroup
import com.diyartaikenov.app.pricecomparator.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(private val productDao: ProductDao): ViewModel() {

    val products:LiveData<List<Product>> = productDao.getProducts().asLiveData()

    fun getProductById(id: Long): LiveData<Product> {
        return productDao.getProduct(id).asLiveData()
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productDao.insert(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productDao.update(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productDao.delete(product)
        }
    }
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
