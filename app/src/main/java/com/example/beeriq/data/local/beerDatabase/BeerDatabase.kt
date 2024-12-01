package com.example.beeriq.data.local.beerDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Beer::class, BeerFts::class], version = 5)
abstract class BeerDatabase: RoomDatabase() {
    abstract val beerDatabaseDao: BeerDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: BeerDatabase? = null

        fun getInstance(context: Context): BeerDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BeerDatabase::class.java,
                        "db_instance"
                    )
                        .fallbackToDestructiveMigration() // Add this line
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}