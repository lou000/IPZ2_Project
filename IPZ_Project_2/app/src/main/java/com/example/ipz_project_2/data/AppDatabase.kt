package com.example.ipz_project_2.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.chatmessage.ChatMessage
import com.example.ipz_project_2.data.chatmessage.ChatMessageDao
import com.example.ipz_project_2.data.chatmessage.UserMessagesCrossRef
import com.example.ipz_project_2.data.chatmessage.UserWithMessages
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactsDao
import com.example.ipz_project_2.data.contact.UserContactsCrossRef
import com.example.ipz_project_2.data.user.UserDao


@Database(
    entities = [ChatMessage::class, Contact::class, User::class, UserContactsCrossRef::class, UserMessagesCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "IPZ2_DB_V4p"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
