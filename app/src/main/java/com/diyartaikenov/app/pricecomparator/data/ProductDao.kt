package com.diyartaikenov.app.pricecomparator.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

import com.diyartaikenov.app.pricecomparator.model.Product

@Dao
interface ProductDao {

    @Query("select * from products")
    fun getProducts(): Flow<List<Product>>
}
