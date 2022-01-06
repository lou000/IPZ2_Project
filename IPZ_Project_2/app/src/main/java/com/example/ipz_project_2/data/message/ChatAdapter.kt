package com.example.ipz_project_2.data.message


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.R
import com.google.firebase.auth.FirebaseAuth

const val SENDER_VIEW_TYPE = 1
const val RECEIVER_VIEW_TYPE = 2

class ChatAdapter(
    private var textMessages: List<TextMessage>
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var text: TextView = itemView.findViewById(R.id.txtMsg)
        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            var message: TextMessage = textMessages[position]
            holder.text.text = message.text

        }
    }

    override fun getItemCount(): Int {
        return textMessages.size
    }

    fun setData(textMessage: List<TextMessage>) {
        this.textMessages = textMessage
        notifyDataSetChanged()
    }
}