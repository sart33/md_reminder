package solutions.mobiledev.reminder.data.reminder.addEmail

import androidx.room.Entity
import androidx.room.PrimaryKey
import solutions.mobiledev.reminder.domain.entity.EmailItem

@Entity(tableName = "email_items")
data class EmailItemDbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var message: String,
    var emails: String,
    var name: String,
    var avatar: String,
    val remId: Int,

)