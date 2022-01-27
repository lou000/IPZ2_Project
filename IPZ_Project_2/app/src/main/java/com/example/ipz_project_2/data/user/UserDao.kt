package com.example.ipz_project_2.data.user

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.contact.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): Flow<User>

    @Query("SELECT * FROM user WHERE userUid == :name")
    fun getUser(name: String): Flow<User>

    @Query("SELECT * FROM user WHERE userUid == :name")
    fun getUser2(name: String): User

    @Insert
    suspend fun insert(user: User)



}