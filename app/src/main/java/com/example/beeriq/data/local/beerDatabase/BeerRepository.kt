package com.example.beeriq.data.local.beerDatabase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class BeerRepository(private val beerDatabaseDao: BeerDatabaseDao) {

    fun insert(beer: Beer) {
        CoroutineScope(IO).launch {
            beerDatabaseDao.insertBeer(beer)
        }
    }

    suspend fun getBeerFullName(fullName: String): List<Beer> {
        return beerDatabaseDao.getBeerFullName(fullName)
    }

    suspend fun searchBeers(query: String): List<Beer> {
        return beerDatabaseDao.searchBeer(query)
    }
}