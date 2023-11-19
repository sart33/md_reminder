package solutions.mobiledev.reminder.domain.reminder.contact.selectList

import solutions.mobiledev.reminder.domain.repository.ContactRepository

class GetContactItemUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(reminderItemId: Int) {
      contactRepository.getContactItem(reminderItemId)
    }
}