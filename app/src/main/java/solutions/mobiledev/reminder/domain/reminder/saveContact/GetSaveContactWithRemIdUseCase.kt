package solutions.mobiledev.reminder.domain.reminder.saveContact

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.domain.repository.EmailRepository
import solutions.mobiledev.reminder.domain.repository.SaveContactRepository

class GetSaveContactWithRemIdUseCase (private val saveContactRepository: SaveContactRepository) {

    operator fun invoke(remId: Int): LiveData<List<SaveContactItem>> {
        return saveContactRepository.getSaveContactWithRemId(remId)
    }
}