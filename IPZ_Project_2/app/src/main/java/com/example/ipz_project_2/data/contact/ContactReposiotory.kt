package com.example.ipz_project_2

import androidx.lifecycle.LiveData
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


class ContactRepository(private val contactsDao: ContactsDao) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val getAllContacts: LiveData<List<Contact>> = contactsDao.getAll()

    suspend fun addContact(contact: Contact) {
        contactsDao.insert(contact)
    }

    suspend fun updateContact(contact: Contact) {
        contactsDao.update(contact)
    }

    suspend fun deleteContact(contact: Contact) {
        contactsDao.delete(contact)
    }

    suspend fun deleteAllContacts(contact: Array<out Contact>) {
        contactsDao.delete(contact)
    }
}

