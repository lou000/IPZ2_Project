package com.example.ipz_project_2.data.chatmessage

import android.app.Application
import androidx.annotation.WorkerThread
import com.example.ipz_project_2.data.AppDatabase
import kotlinx.coroutines.flow.Flow


class ChatMessageRepository(private val chatMessageDao: ChatMessageDao) {

    val allMessages: Flow<MutableList<ChatMessage>> = chatMessageDao.getAll()


    fun contactChat(userID: Long, contactID: Long): Flow<MutableList<ChatMessage>> {
        return chatMessageDao.getContactChat(userID,contactID)
    }

    fun getMsgs(userId: Long):Flow<MutableList<AdapterMessage>>{
        return chatMessageDao.loadMessages(userId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delMsg(messageId: Long) {
        chatMessageDao.delMsg(messageId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(chatMessage: ChatMessage) {
        chatMessageDao.insert(chatMessage)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun remove(chatMessage: ChatMessage) {
        chatMessageDao.delete(chatMessage)
    }
}