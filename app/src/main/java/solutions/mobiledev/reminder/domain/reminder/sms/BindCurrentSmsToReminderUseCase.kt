package solutions.mobiledev.reminder.domain.reminder.sms

import solutions.mobiledev.reminder.domain.repository.EmailRepository
import solutions.mobiledev.reminder.domain.repository.SaveContactRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class BindCurrentSmsToReminderUseCase (private val smsRepository: SmsRepository) {

    suspend operator fun invoke(remId: Int) {
        smsRepository.bindCurrentSmsToReminder(remId)
    }
}