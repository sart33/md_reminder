package solutions.mobiledev.reminder.domain.reminder.menu

import solutions.mobiledev.reminder.domain.repository.ReminderMenuRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class BindCurrentReminderMenuToReminderUseCase (private val reminderMenuRepository: ReminderMenuRepository) {

    suspend operator fun invoke(remId: Int) {
        reminderMenuRepository.bindCurrentReminderMenuToReminder(remId)
    }
}