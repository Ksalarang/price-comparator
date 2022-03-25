package com.diyartaikenov.app.pricecomparator.ui.viewmodel

import androidx.lifecycle.*

import com.diyartaikenov.app.pricecomparator.data.ProductDao
import com.diyartaikenov.app.pricecomparator.model.Product
import com.diyartaikenov.app.pricecomparator.utils.SortOrder
import kotlinx.coroutines.launch

class ProductViewModel(private val productDao: ProductDao): ViewModel() {

    private var sortOrder = SortOrder.DEFAULT

    var products: LiveData<List<Product>> = getProductsSorted()
        private set

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

    fun sortInOrder(sortOrder: SortOrder) {
        this.sortOrder = sortOrder
        products = getProductsSorted()
    }

    private fun getProductsSorted(): LiveData<List<Product>> {
        return when(sortOrder) {
            SortOrder.DEFAULT ->
                productDao.getProducts().asLiveData()
            SortOrder.BY_PROTEIN_PRICE -> {
                productDao.getProductsSortedByProteinPrice().asLiveData()
            }
            SortOrder.BY_PROTEIN_QUANTITY ->
                productDao.getProductsSortedByProteinQuantity().asLiveData()
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
