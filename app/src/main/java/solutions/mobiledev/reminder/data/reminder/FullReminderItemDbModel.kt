package solutions.mobiledev.reminder.data.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FullReminderItemDbModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reminder_items.id")
    var id: Int,

    @ColumnInfo(name = "reminder_items.reminder_timestamp")
    val reminder_timestamp: String,

    @ColumnInfo(name = "reminder_items.date_time")
    val date_time: String,

    @ColumnInfo(name = "reminder_items.first_date_time_reminding")
    val first_date_time_reminding: String,

    @ColumnInfo(name = "reminder_items.body")
    var body: String,

    @ColumnInfo(name = "reminder_items.temporary_remind")
    var temporary_remind: Boolean,

    @ColumnInfo(name = "reminder_items.date_remaining")
    var date_remaining: String,

    @ColumnInfo(name = "reminder_items.time_remaining")
    var time_remaining: String,

    @ColumnInfo(name = "reminder_items.menu_repeat_frequency")
    var menu_repeat_frequency: Int,

    @ColumnInfo(name = "reminder_items.menu_repeat_count")
    var menu_repeat_count: Int,

    @ColumnInfo(name = "reminder_items.menu_timeout")
    var menu_timeout: Int,

    @ColumnInfo(name = "reminder_items.contacts")
    var contacts: Boolean,

    @ColumnInfo(name = "reminder_items.mails")
    var mails: Boolean,

    @ColumnInfo(name = "reminder_items.phone_numbers")
    var phone_numbers: Boolean,

    @ColumnInfo(name = "reminder_items.sms")
    var sms: Boolean,

    @ColumnInfo(name = "reminder_items.image")
    var image: Boolean,

    @ColumnInfo(name = "reminder_items.file")
    var file: Boolean,

    @ColumnInfo(name = "reminder_items.melody")
    var melody: Boolean,

    @ColumnInfo(name = "melody_items.name_name")
    var melody_name: String?,

    @ColumnInfo(name = "melody_items.melody_path")
    var melody_path: String?,

    @ColumnInfo(name = "image_items.name_name")
    var image_name: String?,

    @ColumnInfo(name = "image_items.melody_path")
    var image_path: String?,

    @ColumnInfo(name = "reminder_items.state")
    var state: Boolean,

    @ColumnInfo(name = "reminder_items.show_in_widget")
    var show_in_widget: Boolean,
)