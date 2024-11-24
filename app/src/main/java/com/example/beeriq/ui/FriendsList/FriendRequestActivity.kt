package com.example.beeriq.ui.FriendsList

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.R


class FriendRequestActivity : AppCompatActivity() {
    private lateinit var repo: FirebaseRepo
    private lateinit var listView: ListView
    private lateinit var viewModel: FriendRequestViewModel
    private lateinit var factory: FriendRequestFactory
    private lateinit var friendRequestAdapter: FriendRequestAdapter
    private lateinit var friendRequestList: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        listView = findViewById(R.id.friendRequestList)

        repo = FirebaseRepo(sharedPreferences)
        factory = FriendRequestFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(FriendRequestViewModel::class.java)

        friendRequestList = mutableListOf()

        friendRequestAdapter = FriendRequestAdapter(this, friendRequestList, repo)

        listView.adapter = friendRequestAdapter


        viewModel.data.observe(this) {data ->
            if (data != null){
                friendRequestAdapter.replace(data)
                listView.invalidateViews()
            }
        }
        val backButton: Button = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            finish()
        }
    }
}