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
import com.example.beeriq.databinding.DialogAddFriendBinding
import com.example.beeriq.databinding.FragmentFriendsListBinding

class FriendsList : Fragment() {

    private lateinit var binding: FragmentFriendsListBinding
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
                friendsList.clear()
                friendsList.addAll(data)
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
        val bindingDialog = DialogAddFriendBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        //listener for when user hits add button
        bindingDialog.addFriendDialogButton.setOnClickListener{
            val username = bindingDialog.addFriendInput.editText?.text.toString()
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
            //TODO
            repo.checkIfUserExists(username){
                if (it){
                    repo.sendFriendRequest(username)
                    Toast.makeText(requireContext(), "Friend request sent to ${username}", Toast.LENGTH_SHORT).show()
                }
            }



            dialog.dismiss()
        }

        dialog.show()

    }

}