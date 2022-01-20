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


class UserRepository(application: Application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)

    fun getUser(name: String): Flow<User> = db.userDao().getUser(name)

    fun getUser2(name: String): User = db.userDao().getUser2(name)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addUser(user: User) {
        db.userDao().insert(user)
    }

}

