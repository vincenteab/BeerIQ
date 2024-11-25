package com.example.beeriq.data.local.breweryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Brewery::class], version = 1)
abstract class BreweryDatabase: RoomDatabase() {
    abstract val breweryDatabaseDao: BreweryDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: BreweryDatabase? = null

        fun getInstance(context: Context): BreweryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        BreweryDatabase::class.java, "brewery_db_instance"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}