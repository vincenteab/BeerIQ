package com.example.beeriq.ui.FriendsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FriendListViewModel(private val repo: FirebaseRepo) : ViewModel() {
    val data: LiveData<MutableList<String>> = repo.friendsList

    init {
        println("Debug: Fetching data from viewmodel")
        repo.fetchFriendsListData()
    }

    class FriendListViewModelFactory(private val repo: FirebaseRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FriendListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FriendListViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}