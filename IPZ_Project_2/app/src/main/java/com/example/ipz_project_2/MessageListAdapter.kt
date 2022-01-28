package com.example.ipz_project_2

import com.example.ipz_project_2.data.chatmessage.ChatMessage

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.R
import com.example.ipz_project_2.data.chatmessage.AdapterMessage
import java.text.SimpleDateFormat
import java.util.*

private const val TYPE_TEXT_INCOMING: Int = 0
private const val TYPE_TEXT_OUTGOING: Int = 1
private const val TYPE_VOICE_INCOMING: Int = 2
private const val TYPE_VOICE_OUTGOING: Int = 3

class MessageListAdapter(private var chatMessages: MutableList<AdapterMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class TextIncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: AdapterMessage) {

            val messageText: TextView = itemView.findViewById(R.id.textMessageContent)
            val time: TextView = itemView.findViewById(R.id.timeContent)
            var date: TextView = itemView.findViewById(R.id.dateContent)
            val whoseMessage: TextView = itemView.findViewById(R.id.whoseMessage)
            var whoseMessageContent: TextView = itemView.findViewById(R.id.whoseMessageContent)


            messageText.text = chatMessage.message
            time.text = chatMessage.time
            date.text = SimpleDateFormat("yyyy-MM-dd").format(Date(chatMessage.timestamp))
            whoseMessage.text = "From: "
            whoseMessageContent.text = chatMessage.contactName
        }
    }

    class TextOutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: AdapterMessage) {

            val messageText: TextView = itemView.findViewById(R.id.textMessageContent)
            val time: TextView = itemView.findViewById(R.id.timeContent)
            var date: TextView = itemView.findViewById(R.id.dateContent)
            val whoseMessage: TextView = itemView.findViewById(R.id.whoseMessage)
            var whoseMessageContent: TextView = itemView.findViewById(R.id.whoseMessageContent)


            messageText.text = chatMessage.message
            time.text = chatMessage.time
            date.text = SimpleDateFormat("yyyy-MM-dd").format(Date(chatMessage.timestamp))
            whoseMessage.text = "To: "
            whoseMessageContent.text = chatMessage.contactName
        }
    }


    class VoiceIncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: AdapterMessage) {


            val duration: TextView = itemView.findViewById(R.id.list_duration)
            val time: TextView = itemView.findViewById(R.id.timeContent)
            var date: TextView = itemView.findViewById(R.id.dateContent)
            val whoseMessage: TextView = itemView.findViewById(R.id.whoseMessage)
            val whoseMessageContent: TextView = itemView.findViewById(R.id.whoseMessageContent)


            duration.text = chatMessage.duration
            time.text = chatMessage.time
            date.text = SimpleDateFormat("yyyy-MM-dd").format(Date(chatMessage.timestamp))
            whoseMessage.text = "From: "
            whoseMessageContent.text = chatMessage.contactName


            var im: ImageView = itemView.findViewById(R.id.list_play_button)
            im.setOnClickListener { v: View ->
                val position: Int = absoluteAdapterPosition
                val mediaPlayer = MediaPlayer()
                mediaPlayer.apply {
                    setDataSource(chatMessage.filePath)
                    prepare()
                }

                mediaPlayer.start()
            }
        }
    }

    class VoiceOutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: AdapterMessage) {

            val duration: TextView = itemView.findViewById(R.id.list_duration)
            val time: TextView = itemView.findViewById(R.id.timeContent)
            var date: TextView = itemView.findViewById(R.id.dateContent)
            val whoseMessage: TextView = itemView.findViewById(R.id.whoseMessage)
            val whoseMessageContent: TextView = itemView.findViewById(R.id.whoseMessageContent)



            duration.text = chatMessage.duration
            time.text = chatMessage.time
            date.text = SimpleDateFormat("yyyy-MM-dd").format(Date(chatMessage.timestamp))
            whoseMessage.text = "To: "
            whoseMessageContent.text = chatMessage.contactName


            var im: ImageView = itemView.findViewById(R.id.list_play_button)
            im.setOnClickListener { v: View ->
                val position: Int = absoluteAdapterPosition
                val mediaPlayer = MediaPlayer()
                mediaPlayer.apply {
                    setDataSource(chatMessage.filePath)
                    prepare()
                }
                mediaPlayer.start()
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEXT_INCOMING -> TextIncomingViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.text_message_list_item, viewGroup, false)
            )
            TYPE_TEXT_OUTGOING -> TextOutgoingViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.text_message_list_item, viewGroup, false)
            )
            TYPE_VOICE_INCOMING -> VoiceIncomingViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.voice_message_list_item, viewGroup, false)
            )
            else -> VoiceOutgoingViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.voice_message_list_item, viewGroup, false)
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (chatMessages[position].type) {
            TYPE_TEXT_INCOMING -> (viewHolder as TextIncomingViewHolder).bind(chatMessages[position])
            TYPE_TEXT_OUTGOING -> (viewHolder as TextOutgoingViewHolder).bind(chatMessages[position])
            TYPE_VOICE_INCOMING -> (viewHolder as VoiceIncomingViewHolder).bind(chatMessages[position])
            else -> (viewHolder as VoiceOutgoingViewHolder).bind(chatMessages[position])
        }

    }

    override fun getItemCount() = chatMessages.size

    fun setData(chatMessagesList: MutableList<AdapterMessage>) {
        this.chatMessages = chatMessagesList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatMessages[position].type) {
            0 -> TYPE_TEXT_INCOMING
            1 -> TYPE_TEXT_OUTGOING
            2 -> TYPE_VOICE_INCOMING
            else -> TYPE_VOICE_OUTGOING
        }
    }

    fun removeAt(position: Int): Triple<Long, String?,Int> {
        val id = chatMessages[position].id
        val filePath = chatMessages[position].filePath
        val type = chatMessages[position].type
        chatMessages.removeAt(position)
        notifyItemRemoved(position)
        return Triple(id,filePath,type)
    }
}