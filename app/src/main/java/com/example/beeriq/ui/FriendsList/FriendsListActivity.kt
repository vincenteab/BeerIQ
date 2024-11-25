package com.example.beeriq.ui.FriendsList

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.R
import com.example.beeriq.databinding.FragmentFriendsListBinding

class FriendsListActivity : AppCompatActivity() {
    private lateinit var binding: FragmentFriendsListBinding
    private lateinit var listView: ListView
    private lateinit var viewModel: FriendListViewModel
    private lateinit var friendsListAdapter: FriendListAdapter
    private lateinit var friendsList: MutableList<String>
    private lateinit var outgoingFriendsList: MutableList<String>
    private lateinit var incomingFriendsList: MutableList<String>
    private lateinit var factory: FriendListViewModel.FriendListViewModelFactory
    private lateinit var repo: FirebaseRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentFriendsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = this.getSharedPreferences("UserData", MODE_PRIVATE)


        listView = binding.friendsList

        repo = FirebaseRepo(sharedPreferences)
        factory = FriendListViewModel.FriendListViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(FriendListViewModel::class.java)

        friendsList = mutableListOf()
        outgoingFriendsList = mutableListOf()
        incomingFriendsList = mutableListOf()

        friendsListAdapter = FriendListAdapter(this, friendsList)

        listView.adapter = friendsListAdapter


        viewModel.friendsListData.observe(this) {data ->
            if (data != null){
                friendsList.clear()
                friendsList.addAll(data)
                friendsListAdapter.replace(data)
                listView.invalidateViews()
            }
        }
        viewModel.outgoingFriendsListData.observe(this) {data ->
            if (data != null){
                outgoingFriendsList = data
            }
        }
        viewModel.incomingFriendsListData.observe(this) {data ->
            if (data != null){
                incomingFriendsList = data
            }
        }

        binding.addFriendButton.setOnClickListener{
            showCustomDialog()
        }

        binding.friendRequestButton.setOnClickListener{
            val intent = Intent(this, FriendRequestActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener{
            finish()
        }


    }

    //show custom dialog to add a friend
    private fun showCustomDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_friend)
        val userNameTextEdit = dialog.findViewById<EditText>(R.id.addFriendInput)
        val addButton = dialog.findViewById<Button>(R.id.addFriendDialogButton)

        //listener for when user hits add button
        addButton.setOnClickListener{
            val username = userNameTextEdit.text.toString()
            val sharedPreferences = this.getSharedPreferences("UserData", MODE_PRIVATE)
            val localUser = sharedPreferences.getString("username", null)
            println("debug: friends list $friendsList in FriendsList.kt")

            if(username == localUser){
                Toast.makeText(this, "Cannot add yourself as a friend", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (friendsList.contains(username)){
                Toast.makeText(this, "User is already a friend", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            println("debug: incoming friends list $incomingFriendsList in FriendsList.kt")
            if (incomingFriendsList.contains(username)){
                Toast.makeText(this, "Check friend requests", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            println("debug: outgoing friends list $outgoingFriendsList in FriendsList.kt")
            if (outgoingFriendsList.contains(username)){
                Toast.makeText(this, "Friend request already sent", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //calls function to check if the username exists
            //TODO
            repo.checkIfUserExists(username){
                if (it){
                    repo.sendFriendRequest(username)
                    Toast.makeText(this, "Friend request sent to ${username}", Toast.LENGTH_SHORT).show()
                }
            }



            dialog.dismiss()
        }

        dialog.show()

    }

}