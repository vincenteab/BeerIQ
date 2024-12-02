package com.example.beeriq.ui.userprofile.FriendsList

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.beeriq.R

class FriendListAdapter(private val context: Context, private var friendsList: MutableList<String>) : BaseAdapter() {
    override fun getCount(): Int {
        return friendsList.size
    }

    override fun getItem(position: Int): Any {
        return friendsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.friend_item, null)

        val friendRequestID = view.findViewById(R.id.friendID) as TextView


        val request = friendsList[position]
        friendRequestID.text = request


        return view
    }

    fun replace(friends: MutableList<String>) {
        friendsList = friends
        notifyDataSetChanged()
    }
}