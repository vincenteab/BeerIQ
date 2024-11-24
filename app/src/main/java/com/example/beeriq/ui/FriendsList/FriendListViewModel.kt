package com.example.beeriq.ui.FriendsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FriendListViewModel(private val repo: FirebaseRepo) : ViewModel() {
    val friendsListData: LiveData<MutableList<String>> = repo.friendsList
    val outgoingFriendsListData: LiveData<MutableList<String>> = repo.outgoingFriendsList
    val incomingFriendsListData: LiveData<MutableList<String>> = repo.incomingFriendsList

    init {
        println("Debug: Fetching data from viewmodel")
        repo.fetchAllLists()
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



    class FriendRequestFactory(private val repo: FirebaseRepo) :ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FriendListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FriendListViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}