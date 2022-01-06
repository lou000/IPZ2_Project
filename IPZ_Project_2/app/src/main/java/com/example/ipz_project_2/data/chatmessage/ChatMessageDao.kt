package com.example.ipz_project_2.data.chatmessage

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_message_table ORDER BY timestamp ASC")
    fun getAll(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_message_table WHERE idTo == :userUID OR idFrom == :userUID ORDER BY timestamp ASC")
    fun getContactChat(userUID: Long): Flow<List<ChatMessage>>

    @Insert
    suspend fun insert(vararg chatMessage: ChatMessage)

    @Delete
    suspend fun delete(chatMessage: ChatMessage)

    @Delete
    suspend fun delete(chatMessage: Array<out ChatMessage>)

}