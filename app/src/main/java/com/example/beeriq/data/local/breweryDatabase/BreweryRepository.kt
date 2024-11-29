package com.example.beeriq.data.local.breweryDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BreweryRepository(private val breweryDatabaseDao: BreweryDatabaseDao) {
    val allBreweries: Flow<List<Brewery>> = breweryDatabaseDao.getAllBreweries()

    fun insert(brewery: Brewery) {
        CoroutineScope(IO).launch {
            breweryDatabaseDao.insertBrewery(brewery)
        }
    }

    suspend fun getBreweryFullName(fullName: String): Brewery? {
        return breweryDatabaseDao.getBreweryFullName(fullName)
    }
}