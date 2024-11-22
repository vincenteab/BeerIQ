package com.example.beeriq.ui.FriendsList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class FriendRequestAdapter(private val context: Context, private var requestList: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return requestList.size
    }

    override fun getItem(position: Int): Any {
        return requestList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.friend_request, null)

        val friendRequestID = view.findViewById(R.id.friendRequestID) as TextView


        val request = requestList[position]
        friendRequestID.text = request

        return view
    }

    fun replace(requests: List<String>) {
        requestList = requests
        notifyDataSetChanged()
    }

}