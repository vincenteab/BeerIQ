package com.example.beeriq.ui.FriendsList

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.R
import com.example.beeriq.databinding.ActivityFriendRequestBinding


class FriendRequestActivity : AppCompatActivity() {
    private lateinit var repo: FirebaseRepo
    private lateinit var listView: ListView
    private lateinit var viewModel: FriendListViewModel
    private lateinit var factory: FriendListViewModel.FriendRequestFactory
    private lateinit var friendRequestAdapter: FriendRequestAdapter
    private lateinit var friendRequestList: MutableList<String>
    private lateinit var binding: ActivityFriendRequestBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        listView = binding.friendRequestList

        repo = FirebaseRepo(sharedPreferences)
        factory = FriendListViewModel.FriendRequestFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(FriendListViewModel::class.java)

        friendRequestList = mutableListOf()

        friendRequestAdapter = FriendRequestAdapter(this, friendRequestList, repo)

        listView.adapter = friendRequestAdapter


        viewModel.incomingFriendsListData.observe(this) {data ->
            if (data != null){
                friendRequestAdapter.replace(data)
                listView.invalidateViews()
            }
        }

        binding.backButtonFriendRequests.setOnClickListener {
            finish()
        }

    }
}