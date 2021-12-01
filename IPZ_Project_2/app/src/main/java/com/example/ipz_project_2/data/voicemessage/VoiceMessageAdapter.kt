package com.example.ipz_project_2.data.voicemessage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.R

class VoiceMessageAdapter(var recordings: ArrayList<VoiceMessage>) :
    RecyclerView.Adapter<VoiceMessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username_list_voice_message_list)
        var type: TextView = itemView.findViewById(R.id.type_voice_message_list)
        var date: TextView = itemView.findViewById(R.id.date_voice_message_list)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.voice_message_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position != RecyclerView.NO_POSITION){
            var record: VoiceMessage = recordings[position]

            holder.username.text = "MACIEK record.username"
            holder.type.text = "INCOMING record.type"
            holder.date.text = record.date
        }
    }

    override fun getItemCount(): Int {
        return recordings.size
    }

}