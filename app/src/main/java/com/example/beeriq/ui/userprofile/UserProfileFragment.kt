package com.example.beeriq.ui.userprofile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beeriq.LoginActivity
import com.example.beeriq.R
import com.example.beeriq.ui.userprofile.FriendsList.FriendsListActivity
import com.example.beeriq.ui.userprofile.editprofile.EditProfileFragment


class UserProfileFragment : Fragment(R.layout.fragment_userprofile) {

    private lateinit var username: TextView
    private lateinit var profilePicImgView: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.textView_username)
        profilePicImgView = view.findViewById(R.id.profile_image)
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

        // Navigate to My Posts
        view.findViewById<View>(R.id.option_my_posts).setOnClickListener {
            findNavController().navigate((R.id.ShowMyPostsFragment))
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

        // Fetch the username and profile picture from SharedPreferences
        val usernameKey = sharedPreferences.getString("username", "")
        val profilePic = sharedPreferences.getString("profilePic", "")

        if (usernameKey.isNullOrEmpty()) {
            println("Debug: Username field is empty. Please enter your username to load data.")
            return
        }

        // Update the username TextView dynamically
        username.text = usernameKey

        // Decode and set the profile picture if available
        if (!profilePic.isNullOrEmpty()) {
            val bitmap = decodeBase64ToBitmap(profilePic)
            if (bitmap != null) {
                profilePicImgView.setImageBitmap(bitmap)
            } else {
                println("Debug: Failed to decode profile picture from Base64.")
            }
        } else {
            println("Debug: No profile picture found in SharedPreferences.")
        }
    }


    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}