package com.example.beeriq.ui.FriendsList

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.R
import com.example.beeriq.User
import com.example.beeriq.databinding.FragmentFriendsListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsList : Fragment() {

    private lateinit var binding: FragmentFriendsListBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var viewModel: FriendListViewModel
    private lateinit var friendsListAdapter: FriendListAdapter
    private lateinit var friendsList: MutableList<String>
    private lateinit var outgoingFriendsList: MutableList<String>
    private lateinit var incomingFriendsList: MutableList<String>
    private lateinit var factory: FriendListViewModel.FriendListViewModelFactory
    private lateinit var repo: FirebaseRepo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")
        val sharedPreferences = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)


        listView = binding.friendsList

        repo = FirebaseRepo(sharedPreferences)
        factory = FriendListViewModel.FriendListViewModelFactory(repo)
        viewModel = ViewModelProvider(requireActivity(), factory).get(FriendListViewModel::class.java)

        friendsList = mutableListOf()
        outgoingFriendsList = mutableListOf()
        incomingFriendsList = mutableListOf()

        friendsListAdapter = FriendListAdapter(requireContext(), friendsList)

        listView.adapter = friendsListAdapter


        viewModel.friendsListData.observe(viewLifecycleOwner) {data ->
            if (data != null){
                friendsListAdapter.replace(data)
                listView.invalidateViews()
            }
        }
        viewModel.outgoingFriendsListData.observe(viewLifecycleOwner) {data ->
            if (data != null){
                outgoingFriendsList = data
            }
        }
        viewModel.incomingFriendsListData.observe(viewLifecycleOwner) {data ->
            if (data != null){
                incomingFriendsList = data
            }
        }

        binding.addFriendButton.setOnClickListener{
            showCustomDialog()
        }

        binding.friendRequestButton.setOnClickListener{
            val intent = Intent(requireContext(), FriendRequestActivity::class.java)
            startActivity(intent)
        }



        return binding.root
    }

    //show custom dialog to add a friend
    private fun showCustomDialog(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_friend)
        val userNameTextEdit = dialog.findViewById<EditText>(R.id.addFriendInput)
        val addButton = dialog.findViewById<Button>(R.id.addFriendDialogButton)

        //listener for when user hits add button
        addButton.setOnClickListener{
            val username = userNameTextEdit.text.toString()
            val sharedPreferences = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)
            val localUser = sharedPreferences.getString("username", null)
            println("debug: friends list $friendsList in FriendsList.kt")

            if(username == localUser){
                Toast.makeText(requireContext(), "Cannot add yourself as a friend", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (friendsList.contains(username)){
                Toast.makeText(requireContext(), "User is already a friend", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            println("debug: incoming friends list $incomingFriendsList in FriendsList.kt")
            if (incomingFriendsList.contains(username)){
                Toast.makeText(requireContext(), "Check friend requests", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            println("debug: outgoing friends list $outgoingFriendsList in FriendsList.kt")
            if (outgoingFriendsList.contains(username)){
                Toast.makeText(requireContext(), "Friend request already sent", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //calls function to check if the username exists
            repo.checkIfUserExists(username){
                if (it){
                    addFriend(it)
                }else{
                    Toast.makeText(requireContext(), "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }



            dialog.dismiss()
        }

        dialog.show()

    }

    private fun addFriend(snapshot: DataSnapshot){
        for (friendUser in snapshot.children){
            val friend = friendUser.getValue(User::class.java)
            val sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val localUser = sharedPreferences.getString("username", null)

            //adds friend to the current user's friend list
            firebaseRef.orderByChild("username").equalTo(localUser)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (currentUser in snapshot.children){
                            val userFriends = currentUser.getValue(User::class.java)?.outgoingFriends
                            userFriends?.add(friend?.username.toString())
                            firebaseRef.child(currentUser.key.toString()).child("outgoingFriends").setValue(userFriends)
                            println("debug: current user outgoingFriendsList: $userFriends")
                            val friendIncomingList = friend?.incomingFriends
                            friendIncomingList?.add(localUser.toString())
                            firebaseRef.child(friendUser.key.toString()).child("incomingFriends").setValue(friendIncomingList)
                            println("debug: friend incomingFriendsList: $friendIncomingList")
                            Toast.makeText(requireContext(), "Sent friend request to ${friend?.username}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("debug: error adding friend")
                    }
                })

        }

    }

}