package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class EditDateTimeDateTimeRemindingUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderId: Int, firstDateTimeReminding: String, date: String,
                                time: String, count: Int) {
        reminderRepository.editDateTimeDateTimeRemindingAndCount(reminderId, firstDateTimeReminding, date, time, count)
    }
}