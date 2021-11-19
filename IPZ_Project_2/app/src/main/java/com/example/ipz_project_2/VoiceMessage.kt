package com.example.ipz_project_2

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "voiceMessages")
data class VoiceMessage(
    var filename: String,
    var filePath: String,
    var timestamp: Long,
    var duration: String,
//    var username: String, //TODO DELETE TEM DATA FOR TESTS
    var uidOutgoing: Long,
    var uidIncoming: Long,
    var date: String,
    //TODO ways to indentify users
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    @Ignore
    var isChecked = false

}