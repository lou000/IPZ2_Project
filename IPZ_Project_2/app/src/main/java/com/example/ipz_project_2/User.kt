package com.example.ipz_project_2

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user")
data class User(
    val userUid: String,
    val email: String,
    val phoneNumber: String,
    val privateKey: String

): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L

}
