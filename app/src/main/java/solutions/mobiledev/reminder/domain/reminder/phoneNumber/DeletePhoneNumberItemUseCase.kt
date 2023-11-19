package solutions.mobiledev.reminder.domain.reminder.phoneNumber


import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.PhoneNumberRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class DeletePhoneNumberItemUseCase (private val phoneNumberRepository: PhoneNumberRepository) {
    suspend operator fun invoke(phoneNumberItem: PhoneNumberItem) {
        phoneNumberRepository.deletePhoneNumberItem(phoneNumberItem)
    }
}