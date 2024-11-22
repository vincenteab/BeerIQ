package com.example.beeriq.ui.FriendsList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class FriendRequestActivity : AppCompatActivity() {
    private lateinit var repo: FirebaseRepo
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: FriendRequestViewModel
    private lateinit var factory: FriendRequestFactory
    private lateinit var friendRequestAdapter: FriendRequestAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        recyclerView = findViewById(R.id.friendRequestList)
        repo = FirebaseRepo(sharedPreferences)
        factory = FriendRequestFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(FriendRequestViewModel::class.java)
        recyclerView.layoutManager = LinearLayoutManager(this)
        friendRequestAdapter = FriendRequestAdapter(emptyList())


        viewModel.data.observe(this) {
            friendRequestAdapter = FriendRequestAdapter(it)
            recyclerView.adapter = friendRequestAdapter
        }

    }
}