package solutions.mobiledev.reminder.domain.reminder.contact

import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.repository.ContactRepository

class EditContactItemUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(contactItem: ContactItem) {
        contactRepository.editContactItem(contactItem)

    }

}