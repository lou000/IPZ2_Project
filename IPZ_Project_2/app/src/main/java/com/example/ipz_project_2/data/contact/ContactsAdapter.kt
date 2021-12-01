package com.example.ipz_project_2.data.contact


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.R
import com.example.ipz_project_2.fragments.NewMessageFragmentDirections


class ContactsAdapter(
    private var contacts: List<Contact>,
    val listener: OnItemClickListener,
    val navController: NavController
) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var username: TextView = itemView.findViewById(R.id.contact_list_item_name)
        var contactListItem: ConstraintLayout = itemView.findViewById(R.id.contact_list_item)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            var contact: Contact = contacts[position]
            holder.username.text = contact.name
            holder.contactListItem.setOnClickListener{
                val action = NewMessageFragmentDirections.actionNewMessageFragmentToChatFragment(contact,label = contact.name)
                navController.navigate(action)
            }
        }}

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun setData(contact: List<Contact>) {
        this.contacts = contact
        notifyDataSetChanged()
    }
}