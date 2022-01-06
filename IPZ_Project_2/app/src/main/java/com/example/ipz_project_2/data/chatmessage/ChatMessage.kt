package com.example.ipz_project_2.data.chatmessage

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "chat_message_table")
data class ChatMessage(
    var type: Int,
    var message: String,
    var timestamp: Long,
    var time: String,
    var idTo: Long?,
    var idFrom: Long?,
    var filename: String?,
    var filePath: String?,
    var duration: String?,
    ): Parcelable {
    constructor(): this(-1,"",-1,"",-1,-1,null,null,null)

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L



}