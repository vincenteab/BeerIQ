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

    suspend fun getBeerFullName(fullName: String): Beer? {
        return beerDatabaseDao.getBeerFullName(fullName)
    }
}