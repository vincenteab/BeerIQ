package com.example.beeriq.ui.FriendsList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class FriendRequestAdapter(private val context: Context, private var requestList: MutableList<String>, private val repo: FirebaseRepo) : BaseAdapter() {

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
        val declineButton = view.findViewById<Button>(R.id.declineFriendButton)
        val acceptButton = view.findViewById<Button>(R.id.acceptFriendButton)


        val request = requestList[position]
        friendRequestID.text = request

        acceptButton.setOnClickListener {
            repo.deleteFriendRequest(request)
            notifyDataSetChanged()
            repo.addFriendRequest(request)
            //TODO: add a notify to friends list data
        }

        declineButton.setOnClickListener {
            repo.deleteFriendRequest(request)
            notifyDataSetChanged()
        }
        return view
    }

    fun replace(requests: MutableList<String>) {
        requestList = requests
        notifyDataSetChanged()
    }

}