package com.example.ipz_project_2.data.message

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.w3c.dom.Text
import kotlin.coroutines.CoroutineContext


class MessageRepository(private val messageDao: MessageDao) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val getAllMessages: LiveData<List<TextMessage>> = messageDao.getAll()

    fun getUserMessages(userUID: String): LiveData<List<TextMessage>> {
       return messageDao.getAllUser(userUID)
    }

    suspend fun addMessage(textMessage: TextMessage) {
        messageDao.insert(textMessage)
    }

    suspend fun updateMessage(textMessage: TextMessage) {
        messageDao.update(textMessage)
    }

    suspend fun deleteMessage(textMessage: TextMessage) {
        messageDao.delete(textMessage)
    }

    suspend fun deleteAllMessages(textMessage: Array<out TextMessage>) {
        messageDao.delete(textMessage)
    }
}

