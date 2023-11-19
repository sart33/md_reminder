package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.entity.FullReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetReminderItemWithAllUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderItemId: Int): FullReminderItem {
        return reminderRepository.getReminderItemWithAll(reminderItemId)
    }
}