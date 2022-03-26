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

const val PREF_SORT_ORDER_ORDINAL = "PREF_SORT_ORDER_ORDINAL"
