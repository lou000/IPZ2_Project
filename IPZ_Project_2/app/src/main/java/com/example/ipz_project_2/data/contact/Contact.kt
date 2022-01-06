package com.example.ipz_project_2.data.contact

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contacts")
data class Contact(
    var name: String,
    var phoneNumber: String,
    var uid: String

):Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L



}