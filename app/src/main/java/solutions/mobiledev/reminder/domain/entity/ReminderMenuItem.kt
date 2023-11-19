package solutions.mobiledev.reminder.domain.entity


data class ReminderMenuItem(
    var id: Int = UNDEFINED_ID,
    var remId: Int = UNDEFINED_ID,
    var menuRepeatFrequency: Int = UNDEFINED_ID,
    var menuRepeatCount: Int = UNDEFINED_ID,
    var menuTimeout: Int = UNDEFINED_ID
) {

    companion object {
        const val UNDEFINED_ID = 0
    }
}