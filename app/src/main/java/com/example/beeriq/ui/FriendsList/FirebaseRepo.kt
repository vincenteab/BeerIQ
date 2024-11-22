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
    val localUser = sharedPreferences.getString("username", null)

    private val _incomingFriendsList = MutableLiveData<MutableList<String>>()
    val incomingFriendsList: LiveData<MutableList<String>> get() = _incomingFriendsList

    private val _friendsList = MutableLiveData<MutableList<String>>()
    val friendsList: LiveData<MutableList<String>> get() = _friendsList

    fun fetchIncomingFriendsListData() {
        println("Debug: Fetching data")

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

    fun fetchFriendsListData(){

    }

    fun deleteFriendRequest(friendID: String, onComplete: (Boolean) ->  Unit){
        databaseReference.orderByChild("username").equalTo(localUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val incomingFriendsList = postSnapshot.getValue(User::class.java)?.incomingFriends
                    if (incomingFriendsList != null) {

                        incomingFriendsList.remove(friendID)
                        databaseReference.child(postSnapshot.key!!).child("incomingFriends").setValue(incomingFriendsList)
                        println("debug: friend request ${friendID} removed from incoming friends list")
                        deleteOutgoingFriendRequest(friendID, onComplete)
                        onComplete(true)
                    }else{
                        println("debug: friend request ${friendID} not removed from incoming friends list")
                        onComplete(false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(false)
            }


            fun deleteOutgoingFriendRequest(friendID: String, onComplete: (Boolean) ->  Unit){
                databaseReference.orderByChild("username").equalTo(friendID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val outgoingFriendsList = postSnapshot.getValue(User::class.java)?.outgoingFriends
                            if (outgoingFriendsList != null) {

                                outgoingFriendsList.remove(localUser)
                                databaseReference.child(postSnapshot.key!!).child("outgoingFriends").setValue(outgoingFriendsList)
                                println("debug: friend request ${friendID} removed from outgoing friends list")
                                onComplete(true)
                            }else{
                                println("debug: friend request ${friendID} not removed from outgoing friends list")
                                onComplete(false)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onComplete(false)
                    }
                })
            }

        })


    }

    fun addFriendRequest(friendID: String, onComplete: (Boolean) ->  Unit){
        databaseReference.orderByChild("username").equalTo(friendID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val outgoingFriendsList = postSnapshot.getValue(User::class.java)?.outgoingFriends
                    if (outgoingFriendsList != null) {

                        outgoingFriendsList.add(localUser.toString())
                        databaseReference.child(postSnapshot.key!!).child("outgoingFriends").setValue(outgoingFriendsList)
                        println("debug: friend request ${friendID} added to outgoing friends list")
                        addIncomingFriendRequest(friendID, onComplete)
                        onComplete(true)
                    }else{
                        println("debug: friend request ${friendID} not added to outgoing friends list")
                        onComplete(false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(false)
            }
        })
    }
}