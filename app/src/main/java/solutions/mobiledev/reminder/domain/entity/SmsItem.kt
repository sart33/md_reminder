package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmsItem(
    var id: Int = UNDEFINED_ID,
    val smsMessage: String,
    val smsPhone: String,
    val name: String,
    val avatar: String,
    val remId: Int = UNDEFINED_ID

) : Parcelable  {
    companion object {
        const val UNDEFINED_ID = 0
    }
}