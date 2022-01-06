package com.example.ipz_project_2.data.contact

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ipz_project_2.data.contact.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC") //TODO: OFFSET TO IGNORE FIRST CONTACT -  MAIN USER
    fun getAll(): Flow<List<Contact>>

    @Query("SELECT id FROM contacts WHERE uid == :uid") //TODO: OFFSET TO IGNORE FIRST CONTACT -  MAIN USER
    fun getContactId(uid: String): Long

    @Insert
    suspend fun insert(vararg contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

//    @Delete
//    fun delete(contact: Array<out Contact>)

    @Update
    suspend fun update(contact: Contact)


}