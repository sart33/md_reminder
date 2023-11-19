package solutions.mobiledev.reminder.data.reminder.addSms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_items")
data class SmsItemDbModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var message: String,
    var number: String,
    var name: String,
    var avatar: String,
    val remId: Int,

    )