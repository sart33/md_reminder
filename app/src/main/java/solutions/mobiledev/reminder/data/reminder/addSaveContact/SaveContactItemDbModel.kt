package solutions.mobiledev.reminder.data.reminder.addSaveContact

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save_contact_items")
data class SaveContactItemDbModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var contactId: Int,
    var name: String,
    var number: String,
    var avatar: String,
    val remId: Int,
    )