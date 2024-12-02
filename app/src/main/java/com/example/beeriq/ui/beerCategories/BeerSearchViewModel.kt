package com.example.beeriq.ui.beerCategories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beeriq.data.local.beerDatabase.Beer
import com.example.beeriq.data.local.beerDatabase.BeerRepository
import kotlinx.coroutines.launch

class BeerCategoriesViewModel(private val repository: BeerRepository) : ViewModel() {
    private val _searchResults = MutableLiveData<List<Beer>>()
    val searchResults: LiveData<List<Beer>> get() = _searchResults

    // Method to search beers
    fun searchBeers(query: String) {
        viewModelScope.launch {
            try {
                val results = repository.searchBeers(query) // Query database
                _searchResults.postValue(results) // Post results to LiveData
            } catch (e: Exception) {
                Log.d("testing", "Error searching beers: ${e.message}")
            }
        }
    }
}

class BeerCategoriesViewModelFactory(
    private val repository: BeerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeerCategoriesViewModel::class.java)) {
            return BeerCategoriesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}