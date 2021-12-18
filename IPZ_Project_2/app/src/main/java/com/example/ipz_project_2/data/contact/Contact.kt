package com.example.ipz_project_2.data.contact

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    var name: String,
    var phoneNumber: String,
    var isInDatabase: Boolean

) : Parcelable