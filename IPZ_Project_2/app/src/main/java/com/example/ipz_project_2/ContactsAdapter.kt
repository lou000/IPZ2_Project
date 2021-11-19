package com.example.ipz_project_2


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ContactsAdapter(var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.contact_list_item_name)
        var phoneNumber: TextView = itemView.findViewById(R.id.contact_list_item_phone_number)
        var avatar: ImageView = itemView.findViewById(R.id.contact_list_item_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            var contact: Contact = contacts[position]

            holder.username.text = contact.name
            holder.phoneNumber.text = contact.phoneNumber


        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }


}