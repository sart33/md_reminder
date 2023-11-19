package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class EditDateTimeRemindingAndCountUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderId: Int, firstDateTimeReminding: String, count: Int) {
        reminderRepository.editDateTimeRemindingAndCount(reminderId, firstDateTimeReminding, count)
    }
}