package com.example.beeriq.ui.userprofile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.LoginActivity
import com.example.beeriq.R
import com.example.beeriq.ui.FriendsList.FriendsListActivity
import com.example.beeriq.ui.editprofile.EditProfileFragment
import com.example.beeriq.ui.favorites.FavoritesActivity
import com.example.beeriq.ui.showMyBeers.ShowMyBeersFragment


class UserProfileFragment : Fragment(R.layout.fragment_userprofile) {

    private lateinit var username: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.textView_username)
        loadData()

        // Handle click for "My Beers"
        view.findViewById<View>(R.id.option_my_beers).setOnClickListener {
            findNavController().navigate(R.id.showMyBeersFragment)
        }

        // Handle click for "Account Details"
        view.findViewById<View>(R.id.option_account_details).setOnClickListener {
            // Start EditProfileActivity
            val intent = Intent(requireContext(), EditProfileFragment::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.option_friends).setOnClickListener {
            // Start FriendsActivity
            val intent = Intent(requireContext(), FriendsListActivity::class.java)
            startActivity(intent)
        }

        // Navigate to FavoritesFragment when the "Favorites" option is clicked
        view.findViewById<View>(R.id.option_favorites).setOnClickListener {
            val intent = Intent(requireContext(), FavoritesActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.sign_out).setOnClickListener{
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()
        loadData() // Reload data whenever the fragment becomes visible
    }

    private fun loadData() {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Fetch the username from SharedPreferences
        val usernameKey = sharedPreferences.getString("username", "")

        // Debugging: Log the username being retrieved
        println("Debug: Retrieved username from SharedPreferences: $usernameKey")

        if (usernameKey.isNullOrEmpty()) {
            println("Debug: Username field is empty. Please enter your username to load data.")
            return
        }

        // Update the username TextView dynamically
        username.text = usernameKey
        println("Debug: Username displayed in UserProfileFragment: $usernameKey")
    }

}