package com.example.beeriq.data.local.beerDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.beeriq.ui.beerCategories.BeerCategory

@Dao
interface BeerDatabaseDao {

    @Insert
    suspend fun insertBeer(beer: Beer)

    @Query("SELECT * FROM beer_table WHERE general_category = :category")
    suspend fun getBeersByCategory(category: String): List<Beer>

    @Query("SELECT DISTINCT general_category FROM beer_table")
    suspend fun getDistinctCategories(): List<String>

    @Query("""
        SELECT * FROM beer_table
        JOIN beer_table_fts ON beer_table_fts.beer_full_name == beer_table.beer_full_name
        WHERE beer_table_fts.beer_full_name MATCH :fullName
    """)

    suspend fun getBeerFullName(fullName: String): List<Beer>
}