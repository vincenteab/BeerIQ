package com.example.beeriq

data class User(
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val friends: MutableList<String> = mutableListOf(),
    val outgoingFriends: MutableList<String> = mutableListOf(),
    val incomingFriends: MutableList<String> = mutableListOf()
)