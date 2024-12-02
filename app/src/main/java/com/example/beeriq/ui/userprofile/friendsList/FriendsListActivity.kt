package com.example.beeriq.ui.userprofile.friendsList

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.SharedViewModel
import com.example.beeriq.databinding.DialogAddFriendBinding
import com.example.beeriq.databinding.FragmentFriendsListBinding

class FriendsListActivity : AppCompatActivity() {
    private lateinit var binding: FragmentFriendsListBinding
    private lateinit var listView: ListView
    private lateinit var viewModel: SharedViewModel
    private lateinit var friendsListAdapter: FriendListAdapter
    private lateinit var friendsList: MutableList<String>
    private lateinit var outgoingFriendsList: MutableList<String>
    private lateinit var incomingFriendsList: MutableList<String>
    private lateinit var factory: SharedViewModel.SharedViewModelFactory
    private lateinit var repo: FirebaseRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentFriendsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = this.getSharedPreferences("UserData", MODE_PRIVATE)


        listView = binding.friendsList

        repo = FirebaseRepo(sharedPreferences)
        factory = SharedViewModel.SharedViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(SharedViewModel::class.java)

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
        val bindingDialog = DialogAddFriendBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        //listener for when user hits add postButton
        bindingDialog.addFriendDialogButton.setOnClickListener{
            val username = bindingDialog.addFriendInput.editText?.text.toString()
            val sharedPreferences = this.getSharedPreferences("UserData", MODE_PRIVATE)
            val localUser = sharedPreferences.getString("username", null)
            println("debug: friends list $friendsList in FriendsList.kt")

            if(username == localUser){
                bindingDialog.addFriendInput.error = "Cannot add yourself as a friend"
                return@setOnClickListener
            }

            if (friendsList.contains(username)){
                bindingDialog.addFriendInput.error = "User is already a friend"
                return@setOnClickListener
            }
            println("debug: incoming friends list $incomingFriendsList in FriendsList.kt")
            if (incomingFriendsList.contains(username)){
                bindingDialog.addFriendInput.error = "Friend request already received"
                return@setOnClickListener
            }

            println("debug: outgoing friends list $outgoingFriendsList in FriendsList.kt")
            if (outgoingFriendsList.contains(username)){
                bindingDialog.addFriendInput.error = "Friend request already sent"
                return@setOnClickListener
            }

            //calls function to check if the username exists

            repo.checkIfUserExists(username){
                if (it){
                    repo.sendFriendRequest(username)
                    Toast.makeText(this, "Friend request sent to ${username}", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }




            dialog.dismiss()
        }

        dialog.show()

    }

}