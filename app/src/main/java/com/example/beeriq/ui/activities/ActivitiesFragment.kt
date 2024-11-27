package com.example.beeriq.ui.activities

import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.R
import com.example.beeriq.databinding.FragmentActivitiesBinding
import com.example.beeriq.FirebaseRepo
import java.io.ByteArrayOutputStream
import android.util.Base64
import com.example.beeriq.SharedViewModel


class ActivitiesFragment : Fragment() {
    private lateinit var binding: FragmentActivitiesBinding
    private lateinit var listView: ListView
    private lateinit var viewModel: SharedViewModel
    private lateinit var activitiesAdapter: ActivitiesAdapter
    private lateinit var activitiesList: MutableList<Post>
    private lateinit var friendsList: MutableList<String>
    private lateinit var factory: SharedViewModel.SharedViewModelFactory
    private lateinit var repo: FirebaseRepo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val sharedPreferences = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)

        repo = FirebaseRepo(sharedPreferences)
        listView = binding.activitiesList
        factory = SharedViewModel.SharedViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(SharedViewModel::class.java)

        activitiesList = mutableListOf()

        friendsList = mutableListOf()



        activitiesAdapter = ActivitiesAdapter(requireContext(), activitiesList)
        listView.adapter = activitiesAdapter

        viewModel.friendsListData.observe(viewLifecycleOwner, {
            friendsList.clear()
            friendsList.addAll(it)
            println("debug: activities fragment friends list: $friendsList")
            for (friend in friendsList) {
                repo.fetchActivities(friend)
            }
        })


        viewModel.activitiesListData.observe(viewLifecycleOwner, {
            activitiesList.clear()
            activitiesList.addAll(it)
            activitiesAdapter.replace(activitiesList)
            activitiesAdapter.notifyDataSetChanged()
            val post = activitiesList[0].username
            println("debug: activities fragment activities list: $post")
        })

        return binding.root
    }

    fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, byteArrayOutputStream) // Compress the bitmap
        val byteArray = byteArrayOutputStream.toByteArray() // Convert to ByteArray
        return Base64.encodeToString(byteArray, Base64.DEFAULT) // Convert to Base64 string
    }
}