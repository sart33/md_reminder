package solutions.mobiledev.reminder.domain.reminder.sms

import solutions.mobiledev.reminder.domain.repository.SmsRepository

class DeleteSmsFromRemIdUseCase(private val smsRepository: SmsRepository) {

    suspend operator fun invoke(remId: Int) {
        smsRepository.deleteSmsFromRemId(remId)
    }
}