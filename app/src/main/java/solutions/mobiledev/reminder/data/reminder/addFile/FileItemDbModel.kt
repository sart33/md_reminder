package solutions.mobiledev.reminder.data.reminder.addFile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_items")
data class FileItemDbModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var path: String,
    val remId: Int,

)