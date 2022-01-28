package com.example.ipz_project_2.data.contact

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ipz_project_2.data.contact.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAll(): Flow<MutableList<Contact>>

    @Query("SELECT contactId FROM contacts WHERE contactUid == :uid")
    fun getContactId(uid: String): Flow<Long>

    @Query("SELECT MAX(contactId) FROM contacts")
    fun getLatestId(): Flow<Long>

    @Query("SELECT contactUid FROM contacts WHERE name == :name")
    fun getContactUID(name: String): Flow<String>



    @Transaction
    @Query("SELECT * FROM user WHERE userUid == :name")
    fun getUserWithContacts(name: String): Flow<MutableList<UserWithContacts>>

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

