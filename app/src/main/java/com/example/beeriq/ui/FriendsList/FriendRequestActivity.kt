package com.example.beeriq.ui.FriendsList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.beeriq.R

class FriendRequestActivity : AppCompatActivity() {
    private lateinit var repo: FirebaseRepo
    private lateinit var viewModel: FriendRequestViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        repo = FirebaseRepo(sharedPreferences)
        viewModel = FriendRequestViewModel(repo)
    }
}