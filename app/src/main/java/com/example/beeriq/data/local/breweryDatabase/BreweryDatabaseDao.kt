package com.example.beeriq.data.local.breweryDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreweryDatabaseDao {

    @Insert
    suspend fun insertBrewery(brewery: Brewery)

    @Query("SELECT * FROM brewery_table")
    fun getAllBreweries(): Flow<List<Brewery>>

    @Query("SELECT * FROM brewery_table WHERE LOWER(name) LIKE LOWER(:fullName)")
    suspend fun getBreweryFullName(fullName: String): Brewery?
}