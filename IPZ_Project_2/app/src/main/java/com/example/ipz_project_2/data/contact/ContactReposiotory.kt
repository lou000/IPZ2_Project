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


class ContactRepository(application: Application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)

    val allContacts: Flow<MutableList<Contact>> = db.contactsDao().getAll()

    fun userContacts(name: String): Flow<List<UserWithContacts>> = db.contactsDao().getUserWithContacts(name)


    fun getContactId(uid: String):Long {
        return db.contactsDao().getContactId(uid)
    }

    fun getContactUID(name: String):Flow<String> {
        return db.contactsDao().getContactUID(name)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun joinUserContacts(userWithContacts: UserContactsCrossRef) {
        db.contactsDao().insert(userWithContacts)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addContact(contact: Contact) {
        db.contactsDao().insert(contact)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateContact(contact: Contact) {
        db.contactsDao().update(contact)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteContact(contact: Contact) {
        db.contactsDao().delete(contact)
    }

//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
//    suspend fun deleteAllContacts(contact: Array<out Contact>) {
//        contactsDao.delete(contact)
//    }
}

