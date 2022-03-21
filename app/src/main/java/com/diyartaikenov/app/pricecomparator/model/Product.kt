package com.diyartaikenov.app.pricecomparator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "products")
@TypeConverters(FoodGroupConverter::class)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val weight: Int,
    val price: Int,
    @ColumnInfo(name = "protein_quantity")
    val proteinQuantity: Int,
    @ColumnInfo(name = "food_group")
    val foodGroup: FoodGroup,
    @ColumnInfo(name = "total_protein_quantity")
    val totalProteinQuantity: Int,
    @ColumnInfo(name = "relative_price")
    val relativePrice: Int,
    @ColumnInfo(name = "protein_price")
    val proteinPrice: Double,
)

enum class FoodGroup {
    UNDEFINED,
    ANIMAL_PRODUCTS,
    DIARY,
    FLAVOR_PRODUCTS,
    FRUIT_AND_VEGETABLES,
    GRAIN_PRODUCTS,
}
