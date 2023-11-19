package solutions.mobiledev.reminder.domain.reminder.contact

import solutions.mobiledev.reminder.domain.repository.ContactRepository

class GetContactItemUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(contactItemId: Int) {
      contactRepository.getContactItem(contactItemId)
    }
}