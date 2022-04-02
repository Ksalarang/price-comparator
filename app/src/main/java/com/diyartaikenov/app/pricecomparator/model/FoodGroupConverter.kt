package com.diyartaikenov.app.pricecomparator.model

import androidx.room.TypeConverter
import com.diyartaikenov.app.pricecomparator.utils.FoodGroup

/**
 * Converter for a Room database to store [FoodGroup] enum as [String].
 */
class FoodGroupConverter {

    @TypeConverter
    fun foodGroupToString(foodGroup: FoodGroup): String {
        return foodGroup.name
    }

    @TypeConverter
    fun stringToFoodGroup(value: String): FoodGroup {
        return when (value) {
            FoodGroup.ANIMAL_PRODUCTS.name -> FoodGroup.ANIMAL_PRODUCTS
            FoodGroup.DAIRY.name -> FoodGroup.DAIRY
            FoodGroup.FLAVOR_PRODUCTS.name -> FoodGroup.FLAVOR_PRODUCTS
            FoodGroup.FRUIT_AND_VEGETABLES.name -> FoodGroup.FRUIT_AND_VEGETABLES
            FoodGroup.GRAIN_PRODUCTS.name -> FoodGroup.GRAIN_PRODUCTS
            else -> FoodGroup.UNDEFINED
        }
    }
}
