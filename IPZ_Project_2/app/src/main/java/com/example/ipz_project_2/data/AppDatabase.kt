package com.example.ipz_project_2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ipz_project_2.data.chatmessage.ChatMessage
import com.example.ipz_project_2.data.chatmessage.ChatMessageDao
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactsDao


@Database(
    entities = [ChatMessage::class, Contact::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database3"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
