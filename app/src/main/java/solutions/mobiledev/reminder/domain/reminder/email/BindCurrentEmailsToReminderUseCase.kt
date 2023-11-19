package solutions.mobiledev.reminder.domain.reminder.email

import solutions.mobiledev.reminder.domain.repository.EmailRepository

class BindCurrentEmailsToReminderUseCase (private val emailRepository: EmailRepository) {

    suspend operator fun invoke(remId: Int) {
        emailRepository.bindCurrentEmailsToReminder(remId)
    }
}