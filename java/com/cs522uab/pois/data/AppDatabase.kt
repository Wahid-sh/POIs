package com.cs522uab.pois.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Define the database with PlaceEntity as a table, version 1.
@Database(entities = [PlaceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Returns the singleton instance of AppDatabase.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Build the database instance if it doesn't exist.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pois_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
