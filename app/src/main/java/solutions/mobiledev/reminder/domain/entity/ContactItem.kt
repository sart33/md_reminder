package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactItem(
    var id: Int,
    val name: String,
    var number: String,
    val email: String?,
    val avatar: String?,
    var item: Boolean = DEFAULT_FALSE,
    var lastNumber: Boolean = false
)
    : Parcelable
{

    companion object {
        const val UNDEFINED_ID = 0
        const val DEFAULT_FALSE = false
        const val DEFAULT_NUMBER_IN_LIST = 0

    }
}
