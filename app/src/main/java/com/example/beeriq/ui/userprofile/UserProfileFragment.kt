package com.example.beeriq.ui.userprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.beeriq.R
import com.example.beeriq.ui.editprofile.EditProfileFragment
import com.example.beeriq.ui.favorites.FavoritesActivity


class UserProfileFragment : Fragment(R.layout.fragment_userprofile) {

    private lateinit var username: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.textView_username)
        val sharedPref = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        username.setText(sharedPref.getString("username", ""))

        // Handle click for "Account Details"
        view.findViewById<View>(R.id.option_account_details).setOnClickListener {
            // Start EditProfileActivity
            val intent = Intent(requireContext(), EditProfileFragment::class.java)
            startActivity(intent)
        }

        // Navigate to FavoritesFragment when the "Favorites" option is clicked
        view.findViewById<View>(R.id.option_favorites).setOnClickListener {
            val intent = Intent(requireContext(), FavoritesActivity::class.java)
            startActivity(intent)
        }


    }

}