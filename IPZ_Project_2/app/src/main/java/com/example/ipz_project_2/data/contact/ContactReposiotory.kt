package com.example.ipz_project_2.data.contact

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.ipz_project_2.data.chatmessage.ChatMessage
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext


class ContactRepository(private val contactsDao: ContactsDao) {

    val allContacts: Flow<List<Contact>> = contactsDao.getAll()


    fun getContactId(uid: String):Long {
        return contactsDao.getContactId(uid)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addContact(contact: Contact) {
        contactsDao.insert(contact)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateContact(contact: Contact) {
        contactsDao.update(contact)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteContact(contact: Contact) {
        contactsDao.delete(contact)
    }

//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
//    suspend fun deleteAllContacts(contact: Array<out Contact>) {
//        contactsDao.delete(contact)
//    }
}

