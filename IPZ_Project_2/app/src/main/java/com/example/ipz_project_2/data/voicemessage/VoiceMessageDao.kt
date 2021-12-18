package com.example.ipz_project_2.data.voicemessage

import androidx.room.*

@Dao
interface VoiceMessageDao {
    @Query("SELECT * FROM voiceMessages")
    fun getAll(): List<VoiceMessage>

    @Insert
    fun insert(vararg voiceMessage: VoiceMessage)

    @Delete
    fun delete(voiceMessage: VoiceMessage)

    @Delete
    fun delete(voiceMessages: Array<out VoiceMessage>)

    @Update
    fun update(voiceMessage: VoiceMessage)


}