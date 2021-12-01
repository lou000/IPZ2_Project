package com.example.ipz_project_2.data.contact

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ipz_project_2.data.contact.Contact

@Dao
interface ContactsDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAll(): LiveData<List<Contact>>

    @Insert
    fun insert(vararg contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Delete
    fun delete(contact: Array<out Contact>)

    @Update
    fun update(contact: Contact)


}