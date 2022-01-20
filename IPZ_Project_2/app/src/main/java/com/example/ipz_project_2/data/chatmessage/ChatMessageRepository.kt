package com.example.ipz_project_2.data.chatmessage

import android.app.Application
import androidx.annotation.WorkerThread
import com.example.ipz_project_2.data.AppDatabase
import kotlinx.coroutines.flow.Flow


class ChatMessageRepository(application: Application) {

    private var db: AppDatabase = AppDatabase.getDatabase(application)

    val allMessages: Flow<MutableList<ChatMessage>> = db.chatMessageDao().getAll()


    fun contactChat(userUID: Long): Flow<MutableList<ChatMessage>> {
        return db.chatMessageDao().getContactChat(userUID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delMsg(messageId: Long) {
        db.chatMessageDao().delMsg(messageId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(chatMessage: ChatMessage) {
        db.chatMessageDao().insert(chatMessage)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun remove(chatMessage: ChatMessage) {
        db.chatMessageDao().delete(chatMessage)
    }
}