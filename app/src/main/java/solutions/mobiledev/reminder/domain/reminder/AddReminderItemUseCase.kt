package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class AddReminderItemUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderItem: ReminderItem) {
        reminderRepository.addReminderItem(reminderItem)

    }

}