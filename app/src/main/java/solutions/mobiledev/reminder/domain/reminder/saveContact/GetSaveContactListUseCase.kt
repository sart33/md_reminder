package solutions.mobiledev.reminder.domain.reminder.saveContact

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.domain.repository.SaveContactRepository

class GetSaveContactListUseCase (private val saveContactRepository: SaveContactRepository) {

    operator fun invoke(): LiveData<List<SaveContactItem>> {
        return saveContactRepository.getSaveContactList()
    }
}