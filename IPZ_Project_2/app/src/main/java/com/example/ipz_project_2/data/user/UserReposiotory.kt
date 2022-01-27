package com.example.ipz_project_2.data.user

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.AppDatabase
import com.example.ipz_project_2.data.chatmessage.ChatMessage
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext


class UserRepository(private val userDao: UserDao) {



    fun getUser(name: String): Flow<User> = userDao.getUser(name)

    fun getUser2(name: String): User = userDao.getUser2(name)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addUser(user: User) {
        userDao.insert(user)
    }

}

