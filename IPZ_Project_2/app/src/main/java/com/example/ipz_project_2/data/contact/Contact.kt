package com.example.ipz_project_2.data.contact

import android.os.Parcelable
import androidx.room.*
import com.example.ipz_project_2.User
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contacts")
data class Contact(
    var name: String,
    var phoneNumber: String,
    var uid: String,


):Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}

@Entity(primaryKeys = ["username", "uid"])
data class UserContactsCrossRef(
    val username: String,
    val uid: String
)


data class UserWithContacts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "username",
        entityColumn = "uid",
        associateBy = Junction(UserContactsCrossRef::class)
    )
    val contacts: List<Contact>
)
