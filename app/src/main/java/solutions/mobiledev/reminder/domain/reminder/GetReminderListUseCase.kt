package solutions.mobiledev.reminder.domain.reminder

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetReminderListUseCase(private val reminderRepository: ReminderRepository) {
    operator fun invoke(): LiveData<List<ReminderItem>> {
        return reminderRepository.getReminderList()
    }
}