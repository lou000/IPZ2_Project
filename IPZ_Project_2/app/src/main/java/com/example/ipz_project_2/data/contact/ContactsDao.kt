package com.example.ipz_project_2.data.contact

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ipz_project_2.data.contact.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC") //TODO: OFFSET TO IGNORE FIRST CONTACT -  MAIN USER
    fun getAll(): Flow<MutableList<Contact>>

    @Query("SELECT id FROM contacts WHERE uid == :uid") //TODO: OFFSET TO IGNORE FIRST CONTACT -  MAIN USER
    fun getContactId(uid: String): Long

    @Query("SELECT uid FROM contacts WHERE name == :name")
    fun getContactUID(name: String): Flow<String>


    @Transaction
    @Query("SELECT * FROM user WHERE username == :name")
    fun getUserWithContacts(name: String): Flow<List<UserWithContacts>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: UserContactsCrossRef)

    @Insert
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

//    @Delete
//    fun delete(contact: Array<out Contact>)

    @Update
    suspend fun update(contact: Contact)

}

