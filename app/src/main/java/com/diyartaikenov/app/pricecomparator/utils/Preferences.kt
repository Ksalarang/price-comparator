package com.diyartaikenov.app.pricecomparator.utils

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

fun getIntPreference(activity: Activity, key: String, defaultValue: Int = 0): Int {
    return activity.getPreferences(AppCompatActivity.MODE_PRIVATE)
        .getInt(key, defaultValue)
}

fun saveIntPreference(activity: Activity, key: String, value: Int) {
    activity.getPreferences(AppCompatActivity.MODE_PRIVATE)
        .edit()
        .putInt(key, value)
        .apply()
}

fun getBooleanPreference(activity: Activity, key: String, defaultValue: Boolean = false): Boolean {
    return activity.getPreferences(AppCompatActivity.MODE_PRIVATE)
        .getBoolean(key, defaultValue)
}

fun saveBooleanPreference(activity: Activity, key: String, value: Boolean) {
    activity.getPreferences(AppCompatActivity.MODE_PRIVATE)
        .edit()
        .putBoolean(key, value)
        .apply()
}

fun getFoodGroupsPreference(activity: Activity): List<FoodGroup> {
    val prefs = activity.getPreferences(AppCompatActivity.MODE_PRIVATE)

    val foodGroups = mutableListOf<FoodGroup>()
    val listSize = prefs.getInt(PREF_FOOD_GROUP_LIST_SIZE, 0)

    return if (listSize > 0) {
        for (i in 0 until listSize) {
            val ordinal = prefs.getInt(PREF_FOOD_GROUP + "_$i", 0)
            foodGroups.add(FoodGroup.values()[ordinal])
        }

        foodGroups
    } else {
        FoodGroup.values().asList()
    }
}

fun saveFoodGroupsPreference(activity: Activity, foodGroups: List<FoodGroup>) {
    val prefs = activity.getPreferences(AppCompatActivity.MODE_PRIVATE).edit()
    for (i in foodGroups.indices) {
        prefs.putInt(PREF_FOOD_GROUP + "_$i", foodGroups[i].ordinal)
    }
    prefs.putInt(PREF_FOOD_GROUP_LIST_SIZE, foodGroups.size)
    prefs.apply()
}

fun getStringPreference(
    activity: Activity,
    key: String,
    defaultValue: String? = null
): String? {
    return activity.getPreferences(AppCompatActivity.MODE_PRIVATE)
        .getString(key, defaultValue)
}

fun saveStringPreference(activity: Activity, key: String, value: String) {
    activity.getPreferences(AppCompatActivity.MODE_PRIVATE)
        .edit()
        .putString(key, value)
        .apply()
}

const val PREF_SORT_ORDER_ORDINAL = "SORT_ORDER_ORDINAL"
const val PREF_SHOW_ONLY_PRODUCTS_WITH_PROTEIN = "SHOW ONLY PRODUCTS WITH PROTEIN"

private const val PREF_FOOD_GROUP = "FOOD_GROUP"
private const val PREF_FOOD_GROUP_LIST_SIZE = "FOOD_GROUP_LIST_SIZE"
