package solutions.mobiledev.reminder.data.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity


//@Entity(tableName = "reminder_items")
data class DateDiapason(
    @ColumnInfo(name = "date_remaining")
    var date_remaining: String,

    @ColumnInfo(name = "count(*)")
    var count: String
)
