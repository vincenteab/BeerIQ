package com.example.beeriq.data.local.beerDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BeerDatabaseDao {

    @Insert
    suspend fun insertBeer(beer: Beer)

    @Query("SELECT * FROM beer_table WHERE LOWER(beer_full_name) LIKE LOWER(:fullName)")
    suspend fun getBeerFullName(fullName: String): Beer?
}