package com.example.beeriq


data class User(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val phone: String = "",
    val friends: MutableList<String> = mutableListOf(),
    val outgoingFriends: MutableList<String> = mutableListOf(),
    val incomingFriends: MutableList<String> = mutableListOf()
)