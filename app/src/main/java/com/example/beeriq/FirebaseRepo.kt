package com.example.beeriq

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beeriq.ui.activities.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class FirebaseRepo(private val sharedPreferences: SharedPreferences) {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    val localUser = sharedPreferences.getString("username", null)

    private val fetchScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _incomingFriendsList = MutableLiveData<MutableList<String>>()
    val incomingFriendsList: LiveData<MutableList<String>> get() = _incomingFriendsList

    private val _friendsList = MutableLiveData<MutableList<String>>()
    val friendsList: LiveData<MutableList<String>> get() = _friendsList

    private val _outgoingFriendsList = MutableLiveData<MutableList<String>>()
    val outgoingFriendsList: LiveData<MutableList<String>> get() = _outgoingFriendsList

    private val _activitiesList = MutableLiveData<MutableList<Post>>()
    val activitiesList: LiveData<MutableList<Post>> get() = _activitiesList

    fun fetchAllLists() {
        startRealTimeListeners()
    }

    fun startRealTimeListeners() {
        if (localUser == null) return

        // Listen for incoming friends
        databaseReference.orderByChild("username").equalTo(localUser)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<String>()
                    for (postSnapshot in snapshot.children) {
                        val user = postSnapshot.getValue(User::class.java)
                        if (user != null) {
                            dataList.addAll(user.incomingFriends)
                        }
                    }
                    _incomingFriendsList.postValue(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error fetching incoming friends: ${error.message}")
                }
            })

        // Listen for friends list
        databaseReference.orderByChild("username").equalTo(localUser)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<String>()
                    for (postSnapshot in snapshot.children) {
                        val user = postSnapshot.getValue(User::class.java)
                        if (user != null) {
                            dataList.addAll(user.friends)
                        }
                    }
                    _friendsList.postValue(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error fetching friends list: ${error.message}")
                }
            })

        // Listen for outgoing friends
        databaseReference.orderByChild("username").equalTo(localUser)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<String>()
                    for (postSnapshot in snapshot.children) {
                        val user = postSnapshot.getValue(User::class.java)
                        if (user != null) {
                            dataList.addAll(user.outgoingFriends)
                        }
                    }
                    _outgoingFriendsList.postValue(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error fetching outgoing friends: ${error.message}")
                }
            })
    }

    fun clear() {
        fetchScope.cancel() // Cancel all ongoing coroutines to prevent memory leaks
    }


    // deleteFriendRequest function is used when rejecting a friend request and calls deleteIncomingFriendRequest and deleteOutgoingFriendRequest
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

                                val currentList = currentData.getValue(object : GenericTypeIndicator<List<String>>() {})
                                    ?.toMutableList() ?: mutableListOf()
                                println("debug: current list: $currentList")
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
                                val currentList = currentData.getValue(object : GenericTypeIndicator<List<String>>() {})
                                    ?.toMutableList() ?: mutableListOf()
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


    // addFriendRequest function is used when accepting a friend request and calls addCurrentUserFriend and addFriendUserFriend
    fun acceptFriendRequest(friendID: String){
        addCurrentUserFriend(friendID) {
            if (it) {
                println("Debug: Current user friend $friendID added")
            } else {
                println("Debug: Current user friend $friendID not added")
            }
        }
        addFriendUserFriend(friendID) {
            if (it) {
                println("Debug: Friend user friend $localUser added")
            } else {
                println("Debug: Friend user friend $localUser not added")
            }
        }

    }
    fun addCurrentUserFriend(friendID: String, onComplete: (Boolean) ->  Unit){
        databaseReference.orderByChild("username").equalTo(localUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val userKey = postSnapshot.key
                    if (userKey != null) {
                        val friendsListRef = databaseReference.child(userKey).child("friends")
                        friendsListRef.runTransaction(object : Transaction.Handler{
                            override fun doTransaction(currentData: MutableData): Transaction.Result{

                                val currentList = currentData.getValue(object: GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()
                                if (!currentList.contains(friendID)){
                                    currentList.add(friendID)
                                    currentData.value = currentList
                                }
                                return Transaction.success(currentData)
                            }
                            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                if (committed){
                                    println("debug: friend ${friendID} added to friends list")
                                    onComplete(true)
                                }else{
                                    println("debug: friend ${friendID} not added to friends list")
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
    fun addFriendUserFriend(friendID: String, onComplete: (Boolean) -> Unit){
        databaseReference.orderByChild("username").equalTo(friendID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val friendKey = postSnapshot.key
                    if (friendKey != null) {
                        val friendsListRef = databaseReference.child(friendKey).child("friends")
                        friendsListRef.runTransaction(object : Transaction.Handler{
                            override fun doTransaction(currentData: MutableData): Transaction.Result{
                                val currentList = currentData.getValue(object: GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()
                                if (!currentList.contains(localUser)){

                                    if (localUser != null) {
                                        currentList.add(localUser)
                                    }

                                    currentData.value = currentList
                                }
                                return Transaction.success(currentData)
                            }
                            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                if (committed){
                                    println("debug: friend added ${localUser} to friends list")
                                    onComplete(true)
                                }else{
                                    println("debug: friend not added ${localUser} to friends list")
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

    // function that checks if a user exists in firebase
    fun checkIfUserExists(userId: String, onComplete: (Boolean) -> Unit) {
        // Query the database for the specific user ID
        databaseReference.orderByChild("username").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if the snapshot contains any data
                    val exists = snapshot.exists()
                    if (exists) {
                        println("Debug: User $userId exists.")
                    } else {
                        println("Debug: User $userId does not exist.")
                    }
                    onComplete(exists) // Pass result to callback
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Debug: Failed to check user - ${error.message}")
                    onComplete(false) // Treat as "does not exist" on error
                }
            })
    }

    // sendFriendRequest function is used when sending a friend request and calls sendIncomingFriendRequest and addOutgoingFriendRequest
    fun sendFriendRequest(friendID: String) {
        sendIncomingFriendRequest(friendID) {
            if (it) {
                println("Debug: Incoming friend request sent to $friendID")
            } else {
                println("Debug: Incoming friend request not sent to $friendID")
            }
        }

        addOutgoingFriendRequest(friendID) {
            if (it) {
                println("Debug: Outgoing friend request sent to $friendID")
            } else {
                println("Debug: Outgoing friend request not sent to $friendID")
            }
        }
    }
    fun sendIncomingFriendRequest(friendID: String, onComplete: (Boolean) -> Unit) {
        databaseReference.orderByChild("username").equalTo(friendID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val userKey = postSnapshot.key
                    if (userKey != null) {
                        val friendsListRef = databaseReference.child(userKey).child("incomingFriends")
                        friendsListRef.runTransaction(object : Transaction.Handler{
                            override fun doTransaction(currentData: MutableData): Transaction.Result{

                                val currentList = currentData.getValue(object: GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()
                                if (!currentList.contains(localUser)){
                                    if (localUser != null) {
                                        currentList.add(localUser)
                                    }
                                    currentData.value = currentList
                                }
                                return Transaction.success(currentData)
                            }
                            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                if (committed){
                                    println("debug: added ${localUser} to incoming friends list of ${friendID}")
                                    onComplete(true)
                                }else{
                                    println("debug: not added ${localUser} to incoming friends list of ${friendID}")
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
    fun addOutgoingFriendRequest(friendID: String, onComplete: (Boolean) -> Unit) {
        databaseReference.orderByChild("username").equalTo(localUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val userKey = postSnapshot.key
                    if (userKey != null) {
                        val friendsListRef = databaseReference.child(userKey).child("outgoingFriends")
                        friendsListRef.runTransaction(object : Transaction.Handler{
                            override fun doTransaction(currentData: MutableData): Transaction.Result{

                                val currentList = currentData.getValue(object: GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()
                                if (!currentList.contains(friendID)){
                                    currentList.add(friendID)
                                    currentData.value = currentList
                                }
                                return Transaction.success(currentData)
                            }
                            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                if (committed){
                                    println("debug: friend ${friendID} added to outgoing friends list")
                                    onComplete(true)
                                }else{
                                    println("debug: friend ${friendID} not added to outgoing friends list")
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

    fun fetchActivities(friend: String) {
        databaseReference.orderByChild("username").equalTo(friend)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<Post>()
                    for (postSnapshot in snapshot.children) {
                        val postsPath = postSnapshot.child("posts")
                        if (postsPath.exists()){
                            for (post in postsPath.children){
                                val postObject = post.getValue(Post::class.java)
                                if (postObject != null) {
                                    if (!_activitiesList.value.orEmpty().contains(postObject)) { // Ensure uniqueness
                                        dataList.add(postObject)
                                    }
                                }
                            }
                        }

                    }
                    val currentActivities = _activitiesList.value?.toMutableList() ?: mutableListOf()
                    currentActivities.addAll(dataList)
                    _activitiesList.postValue(currentActivities)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error fetching activities: ${error.message}")
                }
            })
    }

    fun addPost(post: Post) {
        databaseReference.orderByChild("username").equalTo(localUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val userKey = postSnapshot.key
                    if (userKey != null) {
                        val postsRef = databaseReference.child(userKey).child("posts")
                        postsRef.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                val currentList = currentData.getValue(object : GenericTypeIndicator<MutableList<Post>>() {}) ?: mutableListOf()

                                currentList.add(post)
                                currentData.value = currentList
                                return Transaction.success(currentData)
                            }

                            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                if (committed) {
                                    println("debug: post added")
                                } else {
                                    println("debug: post not added")
                                }
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("debug: error - ${error.message}")
            }
        })
    }

    // Update users profile
    fun updateUser(user: User, onComplete: (Boolean) -> Unit) {
        if (user.username.isEmpty()) {
            println("Debug: Username cannot be empty for updating user data.")
            onComplete(false)
            return
        }

        // Locate the user record by username
        databaseReference.orderByChild("username").equalTo(user.username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            // Get the Firebase key for the user
                            val userKey = child.key
                            if (userKey != null) {
                                // Update the user's data in Firebase
                                databaseReference.child(userKey).setValue(user)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            println("Debug: User ${user.username} updated successfully.")
                                            onComplete(true)
                                        } else {
                                            println("Debug: Failed to update user ${user.username}: ${task.exception?.message}")
                                            onComplete(false)
                                        }
                                    }
                                return // Exit after the first match
                            }
                        }
                    } else {
                        println("Debug: No user found with username: ${user.username}")
                        onComplete(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Debug: Failed to locate user ${user.username}: ${error.message}")
                    onComplete(false)
                }
            })
    }


    // Get users data
    fun fetchUserData(user: String, onComplete: (User?) -> Unit) {
        databaseReference.orderByChild("username").equalTo(user)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            val userData = child.getValue(User::class.java)
                            onComplete(userData)
                            return // Exit after finding the first matching user
                        }
                    } else {
                        println("Debug: No user found with username: $user")
                        onComplete(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Debug: Query cancelled: ${error.message}")
                    onComplete(null)
                }
            })
    }



}