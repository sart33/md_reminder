package solutions.mobiledev.reminder.data.reminder

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import solutions.mobiledev.reminder.data.reminder.addMelody.MelodyItemDbModel

@Entity
data class ReminderItemWithMelodyDBModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "reminder_timestamp")
    val reminder_timestamp: String,

    @ColumnInfo(name = "date_time")
    val date_time: String,

    @ColumnInfo(name = "first_date_time_reminding")
    val first_date_time_reminding: String,

    val body: String,

    @ColumnInfo(name = "temporary_remind")
    val temporary_remind: Boolean,

    @ColumnInfo(name = "date_remaining")
    val date_remaining: String,

    @ColumnInfo(name = "time_remaining")
    val time_remaining: String,

    @ColumnInfo(name = "menu_repeat_frequency")
    val menu_repeat_frequency: Int,

    @ColumnInfo(name = "menu_repeat_count")
    val menu_repeat_count: Int,

    @ColumnInfo(name = "menu_timeout")
    val menu_timeout: Int,

    val contacts: Boolean,

    val mails: Boolean,

    @ColumnInfo(name = "phone_numbers")
    val phone_numbers: Boolean,

    val sms: Boolean,

    val image: Boolean,

    val file: Boolean,

    val melody: Boolean,

    val state: Boolean,

    @ColumnInfo(name = "show_in_widget")
    val show_in_widget: Boolean,

    @ColumnInfo(name = "melody_name")
    val melody_name: String?,

    @ColumnInfo(name = "melody_path")
    val melody_path: String?,

    @ColumnInfo(name = "image_name")
    val image_name: String?,

    @ColumnInfo(name = "image_path")
    val image_path: String?
    )
