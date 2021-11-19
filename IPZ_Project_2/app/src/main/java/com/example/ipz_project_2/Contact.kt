package com.example.ipz_project_2

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    var name: String,
    var phoneNumber: String,
    var uid: Long,
    var email: String,

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    @Ignore
    var isChecked = false

}