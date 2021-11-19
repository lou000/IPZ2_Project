package com.example.ipz_project_2

import androidx.room.*

@Dao
interface ContactsDao {
    @Query("SELECT * FROM contacts")
    fun getAll(): List<Contact>

    @Insert
    fun insert(vararg contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Delete
    fun delete(contact: Array<out Contact>)

    @Update
    fun update(contact: Contact)


}