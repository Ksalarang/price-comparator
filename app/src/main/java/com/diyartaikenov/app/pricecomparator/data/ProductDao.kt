package com.diyartaikenov.app.pricecomparator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.diyartaikenov.app.pricecomparator.model.Product

@Dao
interface ProductDao {

    @Query("select * from products")
    fun getProducts(): Flow<List<Product>>

    @Query("select * from products where id = :id")
    fun getProduct(id: Long): Flow<Product>

    @Insert(entity = Product::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)
}
