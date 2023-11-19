package solutions.mobiledev.reminder.data.reminder.addPhoneNumber

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone_number_items")
data class PhoneNumberItemDbModel (

    @PrimaryKey(autoGenerate = true)
        var id: Int,
        var number: String,
        val remId: Int,

    )