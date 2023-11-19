package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class FullReminderItem(

    var id: Int = UNDEFINED_ID,

    val dateTime: LocalDateTime,

    val reminderTime: LocalDateTime,

    val firstDateTimeReminding: LocalDateTime,

    var body: String,

    var temporary: Boolean,

    var dateRemaining: String,

    var timeRemaining: String,

    var menuRepeatFrequency: Int = DEFAULT_VALUE_MENU_REPEAT_FREQUENCY,

    var menuRepeatCount: Int = DEFAULT_VALUE_MENU_REPEAT_COUNT,

    var menuTimeout: Int = DEFAULT_VALUE_MENU_TIMEOUT,

    var contacts: Boolean = STATUS_INACTIVE,

    var mails: Boolean = STATUS_INACTIVE,

    var phoneNumbers: Boolean = STATUS_INACTIVE,

    var sms: Boolean = STATUS_INACTIVE,

    var image: Boolean = STATUS_INACTIVE,

    var file: Boolean = STATUS_INACTIVE,

    var melody: Boolean = STATUS_INACTIVE,

    var melodyName: String? = EMPTY_STRING,

    var melodyPath: String? = EMPTY_STRING,

    var imageName: String? = EMPTY_STRING,

    var imagePath: String? = EMPTY_STRING,

    var state: Boolean = STATUS_ACTIVE,

    var showInWidget: Boolean = STATUS_ACTIVE


) : Parcelable {

    companion object {
        const val UNDEFINED_ID = 0
        const val EMPTY_STRING = ""
        const val STATUS_ACTIVE = true
        const val STATUS_INACTIVE = false
        const val DEFAULT_VALUE_MENU_TIMEOUT = 45
        const val DEFAULT_VALUE_MENU_REPEAT_FREQUENCY = 15
        const val DEFAULT_VALUE_MENU_REPEAT_COUNT = 1
    }
}
