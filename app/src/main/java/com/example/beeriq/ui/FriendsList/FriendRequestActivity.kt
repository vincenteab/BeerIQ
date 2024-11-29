package com.example.beeriq.ui.FriendsList

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.SharedViewModel
import com.example.beeriq.databinding.ActivityFriendRequestBinding


class FriendRequestActivity : AppCompatActivity() {
    private lateinit var repo: FirebaseRepo
    private lateinit var listView: ListView
    private lateinit var viewModel: SharedViewModel
    private lateinit var factory: SharedViewModel.SharedViewModelFactory
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
        factory = SharedViewModel.SharedViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(SharedViewModel::class.java)

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