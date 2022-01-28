package com.example.ipz_project_2.data.chatmessage

import androidx.room.*
import com.example.ipz_project_2.data.contact.UserWithContacts
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_message_table ORDER BY timestamp ASC")
    fun getAll(): Flow<MutableList<ChatMessage>>

    @Query("SELECT * FROM chat_message_table WHERE idUser == :userID AND idContact == :contactID ORDER BY timestamp ASC")
    fun getContactChat(userID: Long, contactID: Long): Flow<MutableList<ChatMessage>>

    @Query("DELETE FROM chat_message_table WHERE id == :messageId")
    suspend fun delMsg(messageId: Long)

    @Insert
    suspend fun insert(vararg chatMessage: ChatMessage)

    @Delete
    suspend fun delete(chatMessage: ChatMessage)

    @Delete
    suspend fun delete(chatMessage: Array<out ChatMessage>)


    @Transaction
    @Query("SELECT * FROM user WHERE userUid == :name")
    fun getUserWithMessages(name: String): Flow<List<UserWithMessages>>

    @Query("SELECT chat_message_table.id AS id, chat_message_table.type AS type, chat_message_table.message  AS message, chat_message_table.timestamp   AS timestamp, chat_message_table.time  AS time, chat_message_table.filePath  AS filePath, chat_message_table.duration  AS duration, contacts.name  AS contactName " + "FROM chat_message_table, contacts " + "WHERE chat_message_table.idContact = contacts.contactId AND chat_message_table.idUser == :userId ")
    fun loadMessages(userId: Long):Flow<MutableList<AdapterMessage>>

}