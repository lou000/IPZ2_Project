package com.example.ipz_project_2.data.chatmessage

import androidx.lifecycle.*
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AppViewModel(
    private val chatRepository: ChatMessageRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val uiScope = CoroutineScope(Dispatchers.Main)

    val allScans: LiveData<List<ChatMessage>> = chatRepository.allMessages.asLiveData()

    fun getUserMessages(userUID: Long): LiveData<List<ChatMessage>> {
        return chatRepository.contactChat(userUID).asLiveData()
    }

    fun getContactId(uid: String): Long {
        return contactRepository.getContactId(uid)
    }

    fun addChatMessage(chatMessage: ChatMessage) = viewModelScope.launch {
        chatRepository.insert(chatMessage)
    }

    val allContacts: LiveData<List<Contact>> = contactRepository.allContacts.asLiveData()

    fun addContact(contact: Contact) = viewModelScope.launch {
        contactRepository.addContact(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        contactRepository.updateContact(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        contactRepository.deleteContact(contact)
    }


}

class AppViewModelFactory(
    private val chatRepository: ChatMessageRepository,
    private val contactRepository: ContactRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(chatRepository, contactRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}