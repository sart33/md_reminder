package solutions.mobiledev.reminder.domain.reminder

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetRemindersCountAtDateUseCase (private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(dateNow: String): Int {
        return reminderRepository.getRemindersCountAtDate(dateNow)
    }
}