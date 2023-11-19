package solutions.mobiledev.reminder.domain.reminder.contact.selectList

import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.repository.ContactRepository

class DeleteContactItemOfSelectListUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(contactItem: ContactItem) {
        contactRepository.deleteContactItemOfSelectList(contactItem)
    }
}