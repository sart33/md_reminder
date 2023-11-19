package solutions.mobiledev.reminder.domain.reminder.sms

import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class AddSmsItemUseCase(private val smsRepository: SmsRepository) {

    suspend operator fun invoke(smsItem: SmsItem) {
        smsRepository.addSmsItem(smsItem)
    }
}