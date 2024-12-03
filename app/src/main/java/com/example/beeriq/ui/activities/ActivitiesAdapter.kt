package com.example.beeriq.ui.activities

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.databinding.ItemActivitiesBinding

class ActivitiesAdapter (private val context: Context, private var postsList: MutableList<Post>) : BaseAdapter() {

    override fun getCount(): Int {
        return postsList.size
    }

    override fun getItem(position: Int): Any {
        return postsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var binding: ItemActivitiesBinding
        if (convertView == null) {
            binding = ItemActivitiesBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ItemActivitiesBinding
        }

        val post = postsList[position]

        binding.userPost.text = post.username
        //TODO: turn post.date into readable string
        binding.datePost.text = post.date

        binding.commentPost.text = post.comment
        binding.stylePost.text = post.subtitle
        binding.beernamePost.text = post.beername

        var image: Bitmap? = base64ToBitmap(post.image)
        binding.imagePost.setImageBitmap(image)



        return binding.root

    }

    fun replace(data: MutableList<Post>) {
        postsList = data
        notifyDataSetChanged()
    }

    // Takes byte array and converts it to an image
    fun base64ToBitmap(base64String: String): Bitmap? {
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}