package com.example.ipz_project_2.data.chatmessage

import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.R

private const val TYPE_TEXT_INCOMING: Int = 0
private const val TYPE_TEXT_OUTGOING: Int = 1
private const val TYPE_VOICE_INCOMING: Int = 2
private const val TYPE_VOICE_OUTGOING: Int = 3

class ChatMessageAdapter(private var chatMessages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class TextIncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: ChatMessage) {
            var messageText: TextView = itemView.findViewById(R.id.incomingText)
            var time: TextView = itemView.findViewById(R.id.incomingTime)

            messageText.text = chatMessage.message
            time.text = chatMessage.time
        }
    }

    class TextOutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: ChatMessage) {
            var messageText: TextView = itemView.findViewById(R.id.outgoingText)
            var time: TextView = itemView.findViewById(R.id.outgoingTime)

            messageText.text = chatMessage.message
            time.text = chatMessage.time
        }
    }


    class VoiceIncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: ChatMessage) {
            var im: ImageView = itemView.findViewById(R.id.incomingPlayImageView)
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
        fun bind(chatMessage: ChatMessage) {
//            var historyItem: TextView = itemView.findViewById(R.id.historyTextView)
            var im: ImageView = itemView.findViewById(R.id.outgoingPlayImageView)
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
                    .inflate(R.layout.chat_text_incoming_item, viewGroup, false)
            )
            TYPE_TEXT_OUTGOING -> TextOutgoingViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.chat_text_outgoing_item, viewGroup, false)
            )
            TYPE_VOICE_INCOMING -> VoiceIncomingViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.chat_voice_incoming_item, viewGroup, false)
            )
            else -> VoiceOutgoingViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.chat_voice_outgoing_item, viewGroup, false)
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

    fun setData(chatMessagesList: List<ChatMessage>) {
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
}