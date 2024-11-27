package com.example.beeriq.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.FirebaseRepo


class ActivitiesViewModel(private val repo: FirebaseRepo) : ViewModel() {
    val friendsListData: LiveData<MutableList<String>> = repo.friendsList
    val activitiesListData: LiveData<MutableList<Post>> = repo.activitiesList

    init {
        repo.fetchAllLists()
    }


    class ActivitiesViewModelFactory(private val repo: FirebaseRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActivitiesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ActivitiesViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}