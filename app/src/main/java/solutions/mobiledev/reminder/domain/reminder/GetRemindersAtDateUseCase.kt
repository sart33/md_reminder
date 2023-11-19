package solutions.mobiledev.reminder.domain.reminder

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetRemindersAtDateUseCase (private val reminderRepository: ReminderRepository) {
    operator fun invoke(date: String): LiveData<List<ReminderItem>> {
        return reminderRepository.getRemindersAtDate(date)
    }
}