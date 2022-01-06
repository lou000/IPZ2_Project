package com.example.ipz_project_2.data.chatmessage

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow


class ChatMessageRepository(private val chatMessageDao: ChatMessageDao) {

    val allMessages: Flow<List<ChatMessage>> = chatMessageDao.getAll()


    fun contactChat(userUID: Long): Flow<List<ChatMessage>> {
        return chatMessageDao.getContactChat(userUID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(chatMessage: ChatMessage) {
        chatMessageDao.insert(chatMessage)
    }
}