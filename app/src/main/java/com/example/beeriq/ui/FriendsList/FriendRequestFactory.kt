package com.example.beeriq.ui.FriendsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FriendRequestFactory(private val repo: FirebaseRepo) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendRequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendRequestViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}