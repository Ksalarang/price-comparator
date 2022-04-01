package com.diyartaikenov.app.pricecomparator.ui.viewmodel

import androidx.lifecycle.*
import com.diyartaikenov.app.pricecomparator.data.ProductDao
import com.diyartaikenov.app.pricecomparator.model.FoodGroup
import com.diyartaikenov.app.pricecomparator.model.Product
import com.diyartaikenov.app.pricecomparator.utils.SortOrder
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class ProductViewModel(private val productDao: ProductDao): ViewModel() {

    var sortOrder = SortOrder.DEFAULT
        private set

    var products: LiveData<List<Product>> =
        productDao.getProductsSortedBy(sortOrder.ordinal).asLiveData()
        private set

    fun getProductById(id: Long): LiveData<Product> {
        return productDao.getProduct(id).asLiveData()
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productDao.insert(product)
        }
    }

    fun addRandomProducts(amount: Int) {
        val products = mutableListOf<Product>()

        repeat(amount) {
            val weight = Random.nextInt(50, 1000)
            val price = Random.nextInt(100, 2000)
            val proteinQuantity = Random.nextInt(0, 50)
            val foodGroup = FoodGroup.values()[Random.nextInt(0, 4)]

            val totalProteinQuantity = (proteinQuantity * (weight / 100.0)).roundToInt()
            val relativePrice = (price / (weight / 100.0)).roundToInt()

            val proteinPrice = if (totalProteinQuantity == 0) {
                0.0
            } else {
                price / totalProteinQuantity.toDouble()
            }

            products.add(Product(
                name = "Product $it",
                weight = weight,
                price = price,
                proteinQuantity = proteinQuantity,
                foodGroup = foodGroup,
                totalProteinQuantity = totalProteinQuantity,
                relativePrice = relativePrice,
                proteinPrice = proteinPrice
            ))
        }

        viewModelScope.launch {
            productDao.insertAll(products)
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

    fun deleteProducts(products: List<Product>) {
        viewModelScope.launch {
            productDao.deleteAll(products)
        }
    }

    fun updateWithParams(sortOrder: SortOrder) {
        this.sortOrder = sortOrder
        products = productDao.getProductsSortedBy(sortOrder.ordinal).asLiveData()
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
