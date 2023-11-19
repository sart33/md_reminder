package solutions.mobiledev.reminder.domain.reminder.menu

import solutions.mobiledev.reminder.domain.repository.ReminderMenuRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class DeleteReminderMenuFromRemIdUseCase(private val reminderMenuRepository: ReminderMenuRepository) {

    suspend operator fun invoke(remId: Int) {
        reminderMenuRepository.deleteReminderMenuFromRemId(remId)
    }
}