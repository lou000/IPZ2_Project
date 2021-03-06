package com.example.ipz_project_2.data.chatmessage

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Looper
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
import java.util.logging.Handler

private const val TYPE_TEXT_INCOMING: Int = 0
private const val TYPE_TEXT_OUTGOING: Int = 1
private const val TYPE_VOICE_INCOMING: Int = 2
private const val TYPE_VOICE_OUTGOING: Int = 3

class ChatMessageAdapter(private var chatMessages: MutableList<ChatMessage>) :
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
            val seekBar: SeekBar = itemView.findViewById(R.id.incomingProgressBar)
            lateinit var runnable: Runnable
            im.setOnClickListener { v: View ->
                val position: Int = absoluteAdapterPosition
                val mediaPlayer = MediaPlayer()
                mediaPlayer.apply {
                    setDataSource(chatMessage.filePath)
                    prepare()
                }

                var handler: android.os.Handler = android.os.Handler(Looper.getMainLooper())
                runnable = Runnable {
                    seekBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(runnable, 100)
                }

                mediaPlayer.start()
                handler.postDelayed(runnable, 100)
                seekBar.max = mediaPlayer.duration

                mediaPlayer.setOnCompletionListener {
                    handler.removeCallbacks(runnable)
                }
            }
        }
    }

    class VoiceOutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatMessage: ChatMessage) {
            val seekBar: SeekBar = itemView.findViewById(R.id.outgoingProgressBar)
            lateinit var runnable: Runnable
            var im: ImageView = itemView.findViewById(R.id.outgoingPlayImageView)
            im.setOnClickListener { v: View ->
                val position: Int = absoluteAdapterPosition
                val mediaPlayer = MediaPlayer()
                mediaPlayer.apply {
                    setDataSource(chatMessage.filePath)
                    prepare()
                }
                var handler: android.os.Handler = android.os.Handler(Looper.getMainLooper())
                runnable = Runnable {
                    seekBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(runnable, 100)
                }

                mediaPlayer.start()
                handler.postDelayed(runnable, 100)
                seekBar.max = mediaPlayer.duration

                mediaPlayer.setOnCompletionListener {
                    handler.removeCallbacks(runnable)
                }
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

    fun setData(chatMessagesList: MutableList<ChatMessage>) {
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

    fun removeAt(position: Int): Triple<Long, String?, Int> {
        val id = chatMessages[position].id
        val filePath = chatMessages[position].filePath
        val type = chatMessages[position].type
        chatMessages.removeAt(position)
        notifyItemRemoved(position)
        return Triple(id, filePath, type)
    }
}