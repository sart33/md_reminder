package solutions.mobiledev.reminder.domain.reminder

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetSomeNextRemindersUseCase(private val reminderRepository: ReminderRepository) {
    operator fun invoke(dateNow: String): List<ReminderItem> {
        return reminderRepository.getSomeNextReminders(dateNow)
    }
}