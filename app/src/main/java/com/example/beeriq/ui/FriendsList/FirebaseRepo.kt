package com.example.beeriq.ui.FriendsList

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beeriq.Activity
import com.example.beeriq.R
import com.example.beeriq.User
import com.example.beeriq.ui.activity.ActivityItem
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
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    fun addActivity(activity: Activity, onComplete: (Boolean) -> Unit) {
        // Ensure localUser is correctly initialized
        if (localUser.isNullOrEmpty()) {
            println("Error: localUser is null or empty")
            onComplete(false)
            return
        }

        // Fetch the current user's information from Firebase
        databaseReference.orderByChild("username").equalTo(localUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if user exists
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        val userKey = postSnapshot.key
                        if (userKey != null) {
                            val activitiesRef = databaseReference.child("users").child(userKey).child("activities")
                            activitiesRef.runTransaction(object : Transaction.Handler {
                                override fun doTransaction(currentData: MutableData): Transaction.Result {
                                    val currentList = currentData.getValue(object : GenericTypeIndicator<MutableList<Activity>>() {})?.toMutableList() ?: mutableListOf()
                                    currentList.add(activity)
                                    currentData.value = currentList
                                    return Transaction.success(currentData)
                                }

                                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                                    if (committed) {
                                        println("Debug: Activity added successfully")
                                        onComplete(true)
                                    } else {
                                        println("Debug: Failed to add activity. Error: ${error?.message}")
                                        onComplete(false)
                                    }
                                }
                            })
                        } else {
                            println("Error: User key is null")
                            onComplete(false)
                        }
                    }
                } else {
                    println("Error: No data found for the user")
                    onComplete(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching data: ${error.message}")
                onComplete(false)
            }
        })
    }

    fun fetchActivities(onComplete: (List<ActivityItem>?) -> Unit) {
        val username = localUser ?: return onComplete(null)

        // Fetch user details first
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        // Get the list of friends and cast it to MutableList<String>
                        val friendsList = userSnapshot.child("friends").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: mutableListOf()

                        val activityItems = mutableListOf<ActivityItem>()

                        // Fetch activities for each friend
                        for (friend in friendsList) {
                            // Query Firebase for friend's activities
                            databaseReference.child("users").child(friend).child("activities")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(activitySnapshot: DataSnapshot) {
                                        activitySnapshot.children.mapNotNull { activitySnapshot ->
                                            val activity = activitySnapshot.getValue(Activity::class.java)
                                            if (activity != null) {
                                                // Add friend activity as ActivityItem
                                                activityItems.add(
                                                    ActivityItem(
                                                        name = friend,  // Assuming friend's username is used for name
                                                        date = activity.date,
                                                        profileImage = R.drawable.baseline_person_24, // Default profile image
                                                        beerImage = R.drawable.ic_dashboard_black_24dp, // Default beer image
                                                        title = activity.Beer,
                                                        subtitle = activity.type,
                                                        comment = activity.comment
                                                    )
                                                )
                                            }
                                        }
                                        // After all activities are fetched, complete with the list
                                        if (activityItems.isNotEmpty()) {
                                            onComplete(activityItems)
                                        } else {
                                            onComplete(null)  // No activities found for friends
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        println("Error fetching friend's activities: ${error.message}")
                                        onComplete(null)
                                    }
                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error fetching user data: ${error.message}")
                    onComplete(null)
                }
            })
    }


}