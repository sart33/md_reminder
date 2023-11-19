package solutions.mobiledev.reminder.domain.reminder.phoneNumber

import solutions.mobiledev.reminder.domain.repository.PhoneNumberRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class DeletePhoneNumberFromRemIdUseCase(private val phoneNumberRepository: PhoneNumberRepository) {

    suspend operator fun invoke(remId: Int) {
        phoneNumberRepository.deletePhoneNumberFromRemId(remId)
    }
}