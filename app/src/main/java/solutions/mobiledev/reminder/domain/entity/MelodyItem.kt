package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MelodyItem(
    var id: Int = UNDEFINED_ID,
    val melodyName: String,
    val melodyPath: String,
    val remId: Int = UNDEFINED_ID

) : Parcelable {
    companion object {
        const val UNDEFINED_ID = 0
    }
}