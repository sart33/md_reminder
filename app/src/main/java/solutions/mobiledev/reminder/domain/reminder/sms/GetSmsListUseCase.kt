package solutions.mobiledev.reminder.domain.reminder.sms

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class GetSmsListUseCase(private val smsRepository: SmsRepository) {

    operator fun invoke(): LiveData<List<SmsItem>> {
        return smsRepository.getSmsList()
    }
}