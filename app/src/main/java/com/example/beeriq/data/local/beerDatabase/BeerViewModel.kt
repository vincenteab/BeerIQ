package com.example.beeriq.data.local.beerDatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BeerViewModel(private val repository: BeerRepository): ViewModel() {
    val beerResult = MutableLiveData<Beer?>()

    fun insert(beer: Beer) {
        repository.insert(beer)
    }
}

class BeerViewModelFactory(private val repository: BeerRepository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeerViewModel::class.java)) {
            return BeerViewModel(repository) as T
        }
        throw IllegalArgumentException("")
    }
}