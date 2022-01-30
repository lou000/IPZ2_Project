package com.example.ipz_project_2.data.chatmessage

import androidx.lifecycle.*
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactRepository
import com.example.ipz_project_2.data.contact.UserContactsCrossRef
import com.example.ipz_project_2.data.contact.UserWithContacts
import com.example.ipz_project_2.data.user.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class AppViewModel(
    private val chatRepository: ChatMessageRepository,
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun user(name: String):LiveData<User> = userRepository.getUser(name).asLiveData()
    fun user2(name: String):User = userRepository.getUser2(name)

    fun usersContacts(name: String):LiveData<MutableList<UserWithContacts>> = contactRepository.userContacts(name).asLiveData()

    fun addUser(user: User) = viewModelScope.launch {
        userRepository.addUser(user)
    }

    fun msgs(userID: Long): LiveData<MutableList<AdapterMessage>> = chatRepository.getMsgs(userID).asLiveData()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    val allScans: LiveData<MutableList<ChatMessage>> = chatRepository.allMessages.asLiveData()

    fun getUserMessages(userID: Long, contactID: Long): LiveData<MutableList<ChatMessage>> {
        return chatRepository.contactChat(userID, contactID).asLiveData()
    }

    fun getContactId(uid: String): LiveData<Long> {
        return contactRepository.getContactId(uid).asLiveData()
    }

    fun getLatestId(): LiveData<Long> {
        return contactRepository.getLatestId().asLiveData()
    }

    fun getContactUID(name: String): LiveData<String> {
        return contactRepository.getContactUID(name).asLiveData()
    }

    fun addChatMessage(chatMessage: ChatMessage) = viewModelScope.launch {
        chatRepository.insert(chatMessage)
    }

    fun deleteChatMessage(chatMessage: ChatMessage) = viewModelScope.launch {
        chatRepository.remove(chatMessage)
    }

    fun  delMsg(messageId: Long) = viewModelScope.launch {
        chatRepository.delMsg(messageId)
    }

    val allContacts: LiveData<MutableList<Contact>> = contactRepository.allContacts.asLiveData()
//    val allContactsUid: LiveData<MutableList<String>> = contactRepository.allContactsUid.asLiveData()

    fun addContact(contact: Contact) = viewModelScope.launch {
        contactRepository.addContact(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        contactRepository.updateContact(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        contactRepository.deleteContact(contact)
    }

    fun joinUserContacts(userWithContacts: UserContactsCrossRef) = viewModelScope.launch {
        contactRepository.joinUserContacts(userWithContacts)
    }

    fun joinUserMessages(userWithContacts: UserContactsCrossRef) = viewModelScope.launch {
        contactRepository.joinUserContacts(userWithContacts)
    }

}

class AppViewModelFactory(
    private val chatRepository: ChatMessageRepository,
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(chatRepository, contactRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}