package com.example.ipz_project_2.data.message

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAll(): LiveData<List<TextMessage>>

    @Query("SELECT * FROM messages WHERE idTo == :userUID OR idFrom == :userUID ORDER BY timestamp ASC")
    fun getAllUser(userUID: String): LiveData<List<TextMessage>>

    @Insert
    suspend fun insert(vararg textMessage: TextMessage)

    @Delete
    suspend fun delete(textMessage: TextMessage)

    @Delete
    suspend fun delete(textMessage: Array<out TextMessage>)

    @Update
    suspend fun update(textMessage: TextMessage)


}
