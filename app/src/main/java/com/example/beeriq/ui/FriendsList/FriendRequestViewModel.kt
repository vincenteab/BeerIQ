package com.example.beeriq.ui.FriendsList


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


class FriendRequestViewModel(private val repo: FirebaseRepo) : ViewModel(){

    val data: LiveData<MutableList<String>> = repo.incomingFriendsList

    init {
            println("Debug: Fetching data from viewmodel")
            repo.fetchIncomingFriendsListData()

    }



}