package solutions.mobiledev.reminder.domain.reminder.phoneNumber

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.repository.PhoneNumberRepository

class GetPhoneNumberWithRemIdUseCase(private val phoneNumberRepository: PhoneNumberRepository) {

    operator fun invoke(remId: Int): LiveData<List<PhoneNumberItem>> {
        return phoneNumberRepository.getPhoneNumberWithRemId(remId)
    }
}