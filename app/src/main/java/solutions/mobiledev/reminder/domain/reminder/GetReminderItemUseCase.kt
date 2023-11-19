package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetReminderItemUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderItemId: Int): ReminderItem {
        return reminderRepository.getReminderItem(reminderItemId)
    }
}