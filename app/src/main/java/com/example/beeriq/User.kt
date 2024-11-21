package com.example.beeriq

data class User(
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val phone: Int? = null,
    val friends: List<String>? = null
)