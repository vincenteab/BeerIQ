package com.example.beeriq.ui.FriendsList

import android.app.Dialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.beeriq.R
import com.example.beeriq.User
import com.example.beeriq.databinding.ActivityLoginBinding
import com.example.beeriq.databinding.FragmentFriendsListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsList : Fragment() {



    private val viewModel: FriendsListViewModel by viewModels()
    private lateinit var binding: FragmentFriendsListBinding
    private lateinit var firebaseRef: DatabaseReference




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")

        binding.addFriendButton.setOnClickListener{
            showCustomDialog()
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

            //calls function to check if the username exists
            checkUsernameExists(username){ exists ->
                if (exists){

                }else{
                    Toast.makeText(requireContext(), "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }


            dialog.dismiss()
        }

        dialog.show()

    }

    //check if the username exists in the database
    private fun checkUsernameExists(username: String, callback: (Boolean) -> Unit){

        //adds listener to database if there is a username that matches the input
        firebaseRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    //if username exists in db
                    if (snapshot.exists()) {
                        println("debug: username exists")
                        callback(true)

                    //if username does not exist in db
                    }else{
                        println("debug: username does not exist")
                        callback(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    private fun addFriend(username: String){
        //add friend to user's friend list

    }

}