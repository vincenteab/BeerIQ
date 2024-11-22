package com.example.beeriq.ui.FriendsList

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beeriq.databinding.FragmentFriendsListBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class FriendRequestViewModel(private val repo: FirebaseRepo) : ViewModel(){

    val data: LiveData<List<String>> = repo.incomingFriendsList

    init {
            println("Debug: Fetching data from viewmodel")
            repo.fetchData()

    }

}