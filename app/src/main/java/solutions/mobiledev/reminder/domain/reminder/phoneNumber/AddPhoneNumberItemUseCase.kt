package solutions.mobiledev.reminder.domain.reminder.phoneNumber

import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.repository.PhoneNumberRepository

class AddPhoneNumberItemUseCase (private val phoneNumberRepository: PhoneNumberRepository) {

    suspend operator fun invoke(phoneItem: PhoneNumberItem) {
        phoneNumberRepository.addPhoneNumberItem(phoneItem)
    }
}