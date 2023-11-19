package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmailItem(
    var id: Int = UNDEFINED_ID,
    val emailMessage: String,
    val emailAddress: String,
    val name: String,
    val avatar: String,
    val remId: Int = UNDEFINED_ID
) : Parcelable {

    companion object {
        const val UNDEFINED_ID = 0
    }
}