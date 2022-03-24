package com.diyartaikenov.app.pricecomparator.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.lang.NumberFormatException

fun Activity.showSoftInput(view: View) {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity.hideSoftInput(view: View) {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun EditText.getIntValue(): Int {
    return try {
        this.text.toString().toInt()
    } catch (e: NumberFormatException) {
        0
    }
}