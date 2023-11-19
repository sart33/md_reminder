package solutions.mobiledev.reminder.data.reminder

import androidx.room.Embedded
import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem

data class ReminderItemWithMelody(
    @Embedded val reminderItem: ReminderItem,
    @Embedded val melodyItem: MelodyItem

)
