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
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
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
        databaseReference.orderByChild("username").equalTo(localUser).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<String>()
                for (postSnapshot in snapshot.children) {

                    val user = postSnapshot.getValue(User::class.java)
                    if (user != null) {
                        for (id in user.friends) {
                            println("debug: friend in friends list: $id")
                            dataList.add(id)
                        }
                    }else{
                        println("Debug: User not found")
                    }


                }
                _friendsList.postValue(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                println("debug: error - ${error.message}")
            }
        })
    }

    fun deleteFriendRequest(friendID: String){
        deleteIncomingFriendRequest(friendID) {
            if (it) {
                println("Debug: Incoming friend request $friendID deleted")
            } else {
                println("Debug: Incoming friend request $friendID not deleted")
            }
        }
        deleteOutgoingFriendRequest(friendID) {
            if (it) {
                println("Debug: Outgoing friend request $friendID deleted")
            } else {
                println("Debug: Outgoing friend request $friendID not deleted")
            }
        }

    }

    fun deleteIncomingFriendRequest(friendID: String, onComplete: (Boolean) ->  Unit){
        databaseReference.orderByChild("username").equalTo(localUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val userKey = postSnapshot.key
                    if (userKey != null) {
                        val incomingFriendsRef = databaseReference.child(userKey).child("incomingFriends")
                        incomingFriendsRef.runTransaction(object : Transaction.Handler{
                            override fun doTransaction(currentData: MutableData): Transaction.Result{
                                val currentList = currentData.getValue() as MutableList<String>
                                if (currentList.contains(friendID)){
                                    currentData.value = currentList.filter { it != friendID }
                                }
                                return Transaction.success(currentData)
                            }
                            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                if (committed){
                                    println("debug: friend request ${friendID} removed from incoming friends list")
                                    onComplete(true)
                                }else{
                                    println("debug: friend request ${friendID} not removed from incoming friends list")
                                    onComplete(false)
                                }
                            }
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("debug: error - ${error.message}")
                onComplete(false)
            }
        })
    }

    fun deleteOutgoingFriendRequest(friendID: String, onComplete: (Boolean) ->  Unit){
        databaseReference.orderByChild("username").equalTo(friendID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val friendKey = postSnapshot.key
                    if (friendKey != null){
                        val outgoingFriendsRef = databaseReference.child(friendKey).child("outgoingFriends")
                        outgoingFriendsRef.runTransaction(object : Transaction.Handler{
                            override fun doTransaction(currentData: MutableData): Transaction.Result{
                                val currentList = currentData.getValue() as MutableList<String>
                                if (currentList.contains(localUser)){
                                    currentData.value = currentList.filter { it != localUser }
                                }
                                return Transaction.success(currentData)
                            }

                            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                if (committed){
                                    println("debug: friend request ${localUser} removed from outgoing friends list")
                                    onComplete(true)
                                }else{
                                    println("debug: friend request ${localUser} not removed from outgoing friends list")
                                    onComplete(false)
                                }
                            }
                        })
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(false)
            }
        })
    }

//    fun addFriendRequest(friendID: String, onComplete: (Boolean) ->  Unit){
//        databaseReference.orderByChild("username").equalTo(friendID).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (postSnapshot in snapshot.children) {
//                    val outgoingFriendsList = postSnapshot.getValue(User::class.java)?.outgoingFriends
//                    if (outgoingFriendsList != null) {
//
//                        outgoingFriendsList.add(localUser.toString())
//                        databaseReference.child(postSnapshot.key!!).child("outgoingFriends").setValue(outgoingFriendsList)
//                        println("debug: friend request ${friendID} added to outgoing friends list")
//                        addIncomingFriendRequest(friendID, onComplete)
//                        onComplete(true)
//                    }else{
//                        println("debug: friend request ${friendID} not added to outgoing friends list")
//                        onComplete(false)
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                onComplete(false)
//            }
//        })
//    }
}