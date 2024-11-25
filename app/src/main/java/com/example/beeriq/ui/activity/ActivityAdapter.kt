package com.example.beeriq.ui.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

data class ActivityItem(
    val name: String,
    val date: String,
    val profileImage: Int,
    val beerImage: Int,
    val title: String,
    val subtitle: String,
    val comment: String
)

class ActivityAdapter(private val activityList: List<ActivityItem>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val beerImage: ImageView = view.findViewById(R.id.beerImage)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val tvComment: TextView = view.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val item = activityList[position]
        holder.profileImage.setImageResource(item.profileImage)
        holder.tvName.text = item.name
        holder.tvDate.text = item.date
        holder.beerImage.setImageResource(item.beerImage)
        holder.tvTitle.text = item.title
        holder.tvSubtitle.text = item.subtitle
        holder.tvComment.text = item.comment
    }

    override fun getItemCount(): Int = activityList.size
}

