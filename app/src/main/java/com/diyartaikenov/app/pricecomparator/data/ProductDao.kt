package com.diyartaikenov.app.pricecomparator.data

import androidx.room.*
import com.diyartaikenov.app.pricecomparator.model.Product
import com.diyartaikenov.app.pricecomparator.utils.FoodGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query(
        "select * from products where food_group in (:foodGroupList)" +
            "order by case " +
            "when :sortOrder = 0 then id " +
            "when :sortOrder = 1 then protein_price " +
            "when :sortOrder = 2 then protein_quantity " +
            "when :sortOrder = 3 then price " +
            "end asc")
    fun getProductsSortedBy(sortOrder: Int, foodGroupList: Array<FoodGroup>): Flow<List<Product>>

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
    suspend fun deleteAll(products: List<Product>)
}
