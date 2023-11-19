package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageItem(
    var id: Int = UNDEFINED_ID,
    val imageName: String,
    val imagePath: String,
    val remId: Int = UNDEFINED_ID

) : Parcelable {
    companion object {
        const val UNDEFINED_ID = 0
    }
}