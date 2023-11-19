package solutions.mobiledev.reminder.domain.reminder.contact.selectList

import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.repository.ContactRepository

class EditContactItemOfSelectListUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(contactItem: ContactItem) {
        contactRepository.editContactItemOfSelectList(contactItem)

    }

}