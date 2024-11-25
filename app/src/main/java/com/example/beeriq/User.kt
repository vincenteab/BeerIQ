package com.example.beeriq

data class User(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val phone: String = "",
    val friends: MutableList<String> = mutableListOf(),
    val outgoingFriends: MutableList<String> = mutableListOf(),
    val incomingFriends: MutableList<String> = mutableListOf(),
    val activities: MutableList<Activity> = mutableListOf() // New field

)

data class Activity(
    val date: String = "",
    val Beer: String = "",
    val type: String = "", // Optional: Add location details if needed
    val comment: String = "" // Optional: Add location details if needed
)