package com.example.ipz_project_2.data.contact

import android.os.Parcelable
import androidx.room.*
import com.example.ipz_project_2.User
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contacts")
data class Contact(
    var name: String,
    var phoneNumber: String,
    var contactUid: String,


):Parcelable {
    @PrimaryKey(autoGenerate = true)
    var contactId: Long = 0L
}

@Entity(primaryKeys = ["userId", "contactId"])
data class UserContactsCrossRef(
    val userId: Long,
    val contactId: Long
)


data class UserWithContacts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "contactId",
        associateBy = Junction(UserContactsCrossRef::class)
    )
    val contacts: MutableList<Contact>
)
