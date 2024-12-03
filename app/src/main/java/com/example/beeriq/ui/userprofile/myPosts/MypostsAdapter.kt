package com.example.beeriq.ui.userprofile.myPosts

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.ui.activities.Post
import com.google.android.material.imageview.ShapeableImageView

class MypostsAdapter(
    private val posts: List<Post>,
    private val context: Context
) : RecyclerView.Adapter<MypostsAdapter.MyPostsViewHolder>() {

    class MyPostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.dateCreated)
        val image: ImageView = itemView.findViewById(R.id.cardImage)
        val title: TextView = itemView.findViewById(R.id.cardTitle)
        val style: TextView = itemView.findViewById(R.id.style)
        val comment: TextView = itemView.findViewById(R.id.comment)
        val profileImageView: ShapeableImageView = itemView.findViewById(R.id.profile_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_posts, parent, false)
        return MyPostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPostsViewHolder, position: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val profilePicBase64 = sharedPreferences.getString("profilePic", "")
        val post = posts[position]

        holder.username.text = post.username
        holder.date.text = post.date
        holder.title.text = post.beername
        holder.style.text = post.subtitle
        holder.comment.text = post.comment

        if (!profilePicBase64.isNullOrEmpty()) {
            // Decode Base64 string to Bitmap
            val bitmap = decodeBase64ToBitmap(profilePicBase64)

            if (bitmap != null) {
                // Set the decoded Bitmap to the ImageView
                holder.profileImageView.setImageBitmap(bitmap)
            } else {
                println("Debug: Failed to decode profile picture.")
            }
        } else {
            println("Debug: No profile picture found. Using default.")
            // Optionally, set a default image
            holder.profileImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        val bitmap = decodeBase64ToBitmap(post.image)
        if (bitmap != null) {
            holder.image.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int = posts.size

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
