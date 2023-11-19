package solutions.mobiledev.reminder.domain.reminder.sms

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class GetSmsWithRemIdUseCase(private val smsRepository: SmsRepository) {

    operator fun invoke(remId: Int): LiveData<List<SmsItem>> {
        return smsRepository.getSmsWithRemId(remId)
    }
}