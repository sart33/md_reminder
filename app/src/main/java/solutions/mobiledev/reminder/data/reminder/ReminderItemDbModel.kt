package solutions.mobiledev.reminder.data.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey
import solutions.mobiledev.reminder.domain.entity.ReminderMenuItem
import org.w3c.dom.Text
import kotlin.time.Duration


@Entity(tableName = "reminder_items")
data class ReminderItemDbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    val reminder_timestamp: String,

    val date_time: String,

    val first_date_time_reminding: String,

    var body: String,

    var temporary_remind: Boolean,

    var date_remaining: String,

    var time_remaining: String,

    var menu_repeat_frequency: Int,

    var menu_repeat_count: Int,

    var menu_timeout: Int,

    var contacts: Boolean,

    var mails: Boolean,

    var phone_numbers: Boolean,

    var sms: Boolean,

    var image: Boolean,

    var file: Boolean,

    var melody: Boolean,

    var state: Boolean,

    var show_in_widget: Boolean,
)
