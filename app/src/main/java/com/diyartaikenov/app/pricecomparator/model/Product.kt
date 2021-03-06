package com.diyartaikenov.app.pricecomparator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.diyartaikenov.app.pricecomparator.utils.FoodGroup

@Entity(tableName = "products")
@TypeConverters(FoodGroupConverter::class)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val weight: Int,
    val price: Int,
    @ColumnInfo(name = "protein_quantity")
    val proteinQuantity: Double,
    @ColumnInfo(name = "food_group")
    val foodGroup: FoodGroup,
    @ColumnInfo(name = "total_protein_quantity")
    val totalProteinQuantity: Double,
    @ColumnInfo(name = "relative_price")
    val relativePrice: Int,
    @ColumnInfo(name = "protein_price")
    val proteinPrice: Double,
)
