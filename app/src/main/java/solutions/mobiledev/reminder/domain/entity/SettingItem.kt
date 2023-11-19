package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingItem(
    var id: Int = UNDEFINED_ID,
    var name: String,
    var value: String,

) : Parcelable {
    companion object {
        const val UNDEFINED_ID = 0
    }
}
