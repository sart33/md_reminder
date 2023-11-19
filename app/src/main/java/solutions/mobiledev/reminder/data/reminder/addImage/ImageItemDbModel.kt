package solutions.mobiledev.reminder.data.reminder.addImage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_items")
data class ImageItemDbModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var image_name: String,
    var image_path: String,
    val rem_id: Int,

)