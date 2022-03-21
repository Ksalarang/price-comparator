package com.diyartaikenov.app.pricecomparator.ui.viewmodel

import androidx.lifecycle.*

import com.diyartaikenov.app.pricecomparator.data.ProductDao
import com.diyartaikenov.app.pricecomparator.model.FoodGroup
import com.diyartaikenov.app.pricecomparator.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(private val productDao: ProductDao): ViewModel() {

    val products:LiveData<List<Product>> = productDao.getProducts().asLiveData()

    fun addProduct(
        name: String,
        weight: Int,
        price: Int,
        proteinQuantity: Int,
        foodGroup: FoodGroup,
        totalProteinQuantity: Int,
        relativePrice: Int,
        proteinPrice: Double
    ) {
        val product = Product(
            name = name,
            weight = weight,
            price = price,
            proteinQuantity = proteinQuantity,
            foodGroup = foodGroup,
            totalProteinQuantity = totalProteinQuantity,
            relativePrice = relativePrice,
            proteinPrice = proteinPrice
        )

        viewModelScope.launch {
            productDao.insert(product)
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
