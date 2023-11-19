package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SaveContactItem(
    var id: Int = UNDEFINED_ID,
    var name: String,
    var number: String,
    var avatar: String,
    val remId: Int = UNDEFINED_ID

) : Parcelable {
    companion object {
        const val UNDEFINED_ID = 0
        const val DEFAULT_FALSE = false
        const val DEFAULT_NUMBER_IN_LIST = 0

    }
}
