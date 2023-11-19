package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetLastInsertIdUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke() : Int {
       return reminderRepository.lastInsertValue()
    }
}