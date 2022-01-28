package com.example.ipz_project_2.data.chatmessage

import android.os.Parcelable
import androidx.room.*
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.contact.Contact
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "chat_message_table")
data class ChatMessage(
    var type: Int,
    var message: String,
    var timestamp: Long,
    var time: String,
    var idContact: Long?,
    var idUser: Long?,
    var filename: String?,
    var filePath: String?,
    var duration: String?,
    var encryptedAESKey: String?
    ): Parcelable {
    constructor(): this(-1,"",-1,"",-1,-1,
        null,null,null, null)

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L



}

data class AdapterMessage(
    var id: Long,
    var type: Int,
    var message: String,
    var timestamp: Long,
    var time: String,
    var date: String?,
    var contactName: String,
    var filePath: String?,
    var duration: String?,
    var encryptedAESKey: String?
)


@Entity(primaryKeys = ["userId", "id"])
data class UserMessagesCrossRef(
    val userId: Long,
    val id: Long,

)


data class UserWithMessages(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id",
        associateBy = Junction(UserMessagesCrossRef::class)
    )
    val messages: List<ChatMessage>
)