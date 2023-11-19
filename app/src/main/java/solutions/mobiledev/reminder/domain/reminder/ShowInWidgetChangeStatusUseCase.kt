package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class ShowInWidgetChangeStatusUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderId: Int, status: Boolean ) {
        reminderRepository.showInWidgetChangeStatus(reminderId, status)

    }
}
