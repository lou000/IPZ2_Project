package com.example.ipz_project_2.data.voicemessage

import androidx.lifecycle.LiveData
import com.example.ipz_project_2.data.voicemessage.VoiceMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class VoiceMessageRepository(private val messageDao: VoiceMessageDao) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    val getAllMessages: LiveData<List<VoiceMessage>> = messageDao.getAll()

    fun addMessage(msg: VoiceMessage) {
        messageDao.insert(msg)
    }

    fun deleteMessage(msg: VoiceMessage) {
        messageDao.delete(msg)
    }

    fun deleteAllMessages(msg: Array<out VoiceMessage>) {
        messageDao.delete(msg)
    }
}