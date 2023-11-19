package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class CheckDateUseCase(private val reminderRepository: ReminderRepository) {

    operator fun invoke() {
        reminderRepository.checkDate()
    }
}