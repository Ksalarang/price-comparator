package com.diyartaikenov.app.pricecomparator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.diyartaikenov.app.pricecomparator.model.Product

@Database(
    version = 4,
    entities = [Product::class],
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getProductDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_3_4 = object: Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "update products set food_group = case " +
                            "when food_group == 0 then 'UNDEFINED' " +
                            "when food_group == 1 then 'ANIMAL_PRODUCTS' " +
                            "when food_group == 2 then 'DAIRY' " +
                            "when food_group == 3 then 'FLAVOR_PRODUCTS' " +
                            "when food_group == 4 then 'FRUIT_AND_VEGETABLES' " +
                            "when food_group == 5 then 'GRAIN_PRODUCTS' " +
                            "else food_group end"
                )
            }
        }
    }
}
