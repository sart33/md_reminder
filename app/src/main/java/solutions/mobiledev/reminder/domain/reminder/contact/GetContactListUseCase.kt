package solutions.mobiledev.reminder.domain.reminder.contact

import androidx.lifecycle.MutableLiveData
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.repository.ContactRepository

class GetContactListUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(contactItemId: Int): MutableLiveData<List<ContactItem>> {
        return contactRepository.getContactList()
    }

}