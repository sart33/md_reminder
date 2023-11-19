package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class ActiveChangeStatusUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminderId: Int, status: Boolean) {
        reminderRepository.activeChangeStatus(reminderId, status)
    }
}