package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarDate(
    var id: Int = UNDEFINED_ID,
    var date: String,
    var dayOff: Boolean = DEFAULT_FALSE,
    var month: String,
    var current: Boolean = DEFAULT_FALSE,
    var eventCount: Int = NONE_EVENT,
) : Parcelable {

    companion object {
        const val UNDEFINED_ID = 0
        const val NONE_EVENT = 0
        const val DEFAULT_FALSE = false
    }
}