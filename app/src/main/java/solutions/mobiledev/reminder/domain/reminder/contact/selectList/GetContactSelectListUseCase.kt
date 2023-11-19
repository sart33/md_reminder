package solutions.mobiledev.reminder.domain.reminder.contact.selectList

import androidx.lifecycle.MutableLiveData
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.repository.ContactRepository

class GetContactSelectListUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(contactItemId: Int): MutableLiveData<List<ContactItem>> {
        return contactRepository.getContactSelectList()
    }

}