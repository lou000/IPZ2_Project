package com.example.ipz_project_2.data.message

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "messages")
data class TextMessage(
    var text: String,
    var timestamp: Long,
    var date: String,
    var idTo: String,
    var idFrom: String,



):Parcelable {
    constructor(): this("",-1,"","","")

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L



}