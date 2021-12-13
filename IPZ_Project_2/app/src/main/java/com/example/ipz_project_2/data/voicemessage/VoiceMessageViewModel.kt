package com.example.ipz_project_2.data.voicemessage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ipz_project_2.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoiceMessageViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<VoiceMessage>>
    private val repository: VoiceMessageRepository

    init {
        val msgDao = AppDatabase.getInstance(application).voiceMessageDao
        repository = VoiceMessageRepository(msgDao)
        readAllData = repository.getAllMessages
    }

    fun addContact(msg: VoiceMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMessage(msg)
        }
    }

    fun deleteContact(msg: VoiceMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMessage(msg)
        }
    }

    fun deleteAllContacts(msg: Array<out VoiceMessage>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllMessages(msg)
        }
    }
}