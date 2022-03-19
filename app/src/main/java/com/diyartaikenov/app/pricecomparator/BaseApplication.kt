package com.diyartaikenov.app.pricecomparator

import android.app.Application
import com.diyartaikenov.app.pricecomparator.data.AppDatabase

class BaseApplication: Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
