package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class EditDateTimeRemindingUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderId: Int, firstDateTimeReminding: String) {
        reminderRepository.editDateTimeReminding(reminderId, firstDateTimeReminding)
    }
}