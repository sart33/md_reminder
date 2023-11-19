package solutions.mobiledev.reminder.data.reminder.addMelody

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "melody_items")
data class MelodyItemDbModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var melody_name: String,
    var melody_path: String,
    val rem_id: Int,

)