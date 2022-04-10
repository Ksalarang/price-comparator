package com.diyartaikenov.app.pricecomparator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.diyartaikenov.app.pricecomparator.model.Product

@Database(
    version = 5,
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
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_4_5 = object: Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "create table products_new (" +
                            "id INTEGER NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "weight INTEGER NOT NULL, " +
                            "price INTEGER NOT NULL, " +
                            "protein_quantity REAL NOT NULL, " +
                            "food_group TEXT NOT NULL, " +
                            "total_protein_quantity REAL NOT NULL, " +
                            "relative_price INTEGER NOT NULL, " +
                            "protein_price REAL NOT NULL, " +
                            "primary key(id))"
                )
                database.execSQL(
                    "insert into products_new (id, name, weight, price, protein_quantity, " +
                            "food_group, total_protein_quantity, relative_price, protein_price) " +
                            "select id, name, weight, price, " +
                            "cast(protein_quantity as REAL), " +
                            "food_group, " +
                            "cast(total_protein_quantity as REAL), " +
                            "relative_price, " +
                            "protein_price " +
                            "from products"
                )
                database.execSQL(
                    "drop table products"
                )
                database.execSQL(
                    "alter table products_new rename to products"
                )
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
