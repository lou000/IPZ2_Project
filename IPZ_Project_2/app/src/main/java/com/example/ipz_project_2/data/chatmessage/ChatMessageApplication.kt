package com.example.ipz_project_2.data.chatmessage

import android.app.Application
import com.example.ipz_project_2.data.AppDatabase
import com.example.ipz_project_2.data.contact.ContactRepository
import com.example.ipz_project_2.data.user.UserRepository


class ChatMessageApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val chatRepository by lazy { ChatMessageRepository(database.chatMessageDao()) }
    val contactRepository by lazy { ContactRepository(database.contactsDao()) }
    val userRepository by lazy { UserRepository(database.userDao()) }
}