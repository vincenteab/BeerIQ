package com.example.beeriq.ui.activities

data class Post(
    val username: String,
    val date: String,
    val image: ByteArray,
    val beername: String,
    val subtitle: String,
    val comment: String
)