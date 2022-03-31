package com.diyartaikenov.app.pricecomparator.model

import androidx.room.TypeConverter

/**
 * Converter for a Room database to store [FoodGroup] enums as integers.
 */
class FoodGroupConverter {

    @TypeConverter
    fun foodGroupToInt(foodGroup: FoodGroup): Int {
        return foodGroup.ordinal
    }

    @TypeConverter
    fun intToFoodGroup(value: Int): FoodGroup {
        return when (value) {
            FoodGroup.ANIMAL_PRODUCTS.ordinal -> FoodGroup.ANIMAL_PRODUCTS
            FoodGroup.DAIRY.ordinal -> FoodGroup.DAIRY
            FoodGroup.FLAVOR_PRODUCTS.ordinal -> FoodGroup.FLAVOR_PRODUCTS
            FoodGroup.FRUIT_AND_VEGETABLES.ordinal -> FoodGroup.FRUIT_AND_VEGETABLES
            FoodGroup.GRAIN_PRODUCTS.ordinal -> FoodGroup.GRAIN_PRODUCTS
            else -> FoodGroup.UNDEFINED
        }
    }
}
