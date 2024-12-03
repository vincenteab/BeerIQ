package com.example.beeriq.data.local.breweryDatabase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BreweryViewModel(private val repository: BreweryRepository): ViewModel() {
    val allBreweries: LiveData<List<Brewery>> = repository.allBreweries.asLiveData()
    val breweryResult = MutableLiveData<Brewery?>()

    fun insert(brewery: Brewery) {
        Log.d("BREWERY NAME: ", brewery.name)
        Log.d("DESCRIPTION TITLE: ", brewery.descriptionTitle)
        repository.insert(brewery)
    }

    fun getBreweryFullName(fullName: String): LiveData<Brewery?> = liveData {
        viewModelScope.launch {
            val brewery = repository.getBreweryFullName(fullName)
            breweryResult.value = brewery
        }
    }
}

class BreweryViewModelFactory(private val repository: BreweryRepository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BreweryViewModel::class.java)) {
            return BreweryViewModel(repository) as T
        }
        throw IllegalArgumentException("")
    }
}