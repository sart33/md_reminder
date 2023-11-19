package solutions.mobiledev.reminder.domain.reminder.contact

import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.repository.ContactRepository

class DeleteContactItemUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactItem: ContactItem) {
        contactRepository.deleteContactItem(contactItem)
    }
}