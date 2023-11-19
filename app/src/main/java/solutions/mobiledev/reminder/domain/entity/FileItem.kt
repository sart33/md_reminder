package solutions.mobiledev.reminder.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileItem(var id: Int = UNDEFINED_ID,
                    val name: String,
                    val path: String,
                    val remId: Int = UNDEFINED_ID

) : Parcelable {

    companion object {
        const val UNDEFINED_ID = 0
    }
}