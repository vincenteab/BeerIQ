package com.example.beeriq

import com.example.beeriq.ui.activities.Post


data class User(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val friends: MutableList<String> = mutableListOf(),
    val outgoingFriends: MutableList<String> = mutableListOf(),
    val incomingFriends: MutableList<String> = mutableListOf(),
    val posts: MutableList<Post> = mutableListOf(),
)