package com.example.beeriq.ui.FriendsList

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beeriq.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepo(private val sharedPreferences: SharedPreferences) {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")


    private val _incomingFriendsList = MutableLiveData<List<String>>()
    val incomingFriendsList: LiveData<List<String>> get() = _incomingFriendsList

    fun fetchData() {
        println("Debug: Fetching data")
        val localUser = sharedPreferences.getString("username", null)
        databaseReference.orderByChild("username").equalTo(localUser).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<String>()
                for (postSnapshot in snapshot.children) {

                    val user = postSnapshot.getValue(User::class.java)
                    if (user != null) {
                        for (id in user.incomingFriends) {
                            println("debug: incoming friend id: $id")
                            dataList.add(id)
                        }
                    }else{
                        println("Debug: User not found")
                    }


                }
                _incomingFriendsList.postValue(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                println("debug: error - ${error.message}")
            }
        })
    }
}