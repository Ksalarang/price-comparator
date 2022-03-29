package com.diyartaikenov.app.pricecomparator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.diyartaikenov.app.pricecomparator.model.Product

@Dao
interface ProductDao {

    @Query("select * from products")
    fun getProducts(): Flow<List<Product>>

    @Query("select * from products where protein_quantity > 0 " +
            "order by protein_price asc")
    fun getProductsSortedByProteinPrice(): Flow<List<Product>>

    @Query("select * from products where protein_quantity > 0 " +
            "order by total_protein_quantity desc")
    fun getProductsSortedByTotalProteinQuantity(): Flow<List<Product>>

    @Query("select * from products order by price asc")
    fun getProductsSortedByPrice(): Flow<List<Product>>

    @Query("select * from products where id = :id")
    fun getProduct(id: Long): Flow<Product>

    @Insert(entity = Product::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Insert(entity = Product::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Delete
    suspend fun delete(products: List<Product>)
}
