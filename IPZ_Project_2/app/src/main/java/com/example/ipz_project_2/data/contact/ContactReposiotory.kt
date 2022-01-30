package com.example.ipz_project_2.data.contact

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.ipz_project_2.data.AppDatabase
import com.example.ipz_project_2.data.chatmessage.ChatMessage
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext


class ContactRepository(private  val contactsDao: ContactsDao) {



    val allContacts: Flow<MutableList<Contact>> = contactsDao.getAll()
//    val allContactsUid: Flow<MutableList<String>> = contactsDao.getAllUid()
    fun userContacts(name: String): Flow<MutableList<UserWithContacts>> = contactsDao.getUserWithContacts(name)


    fun getContactId(uid: String):Flow<Long> {
        return contactsDao.getContactId(uid)
    }

    fun getLatestId():Flow<Long> {
        return contactsDao.getLatestId()
    }



    fun getContactUID(name: String):Flow<String> {
        return contactsDao.getContactUID(name)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun joinUserContacts(userWithContacts: UserContactsCrossRef) {
        contactsDao.insert(userWithContacts)
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

