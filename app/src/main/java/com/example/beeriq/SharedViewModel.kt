package com.example.beeriq

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.ui.activities.Post

class SharedViewModel(private val repo: FirebaseRepo) : ViewModel() {
    val friendsListData: LiveData<MutableList<String>> = repo.friendsList
    val outgoingFriendsListData: LiveData<MutableList<String>> = repo.outgoingFriendsList
    val incomingFriendsListData: LiveData<MutableList<String>> = repo.incomingFriendsList
    val activitiesListData: LiveData<MutableList<Post>> = repo.activitiesList

    init {
        println("Debug: Fetching data from viewmodel")
        repo.fetchAllLists()
    }

    class SharedViewModelFactory(private val repo: FirebaseRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}