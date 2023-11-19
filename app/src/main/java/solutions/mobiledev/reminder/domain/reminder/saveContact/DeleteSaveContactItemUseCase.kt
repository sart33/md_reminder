package solutions.mobiledev.reminder.domain.reminder.saveContact

import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.domain.repository.SaveContactRepository

class DeleteSaveContactItemUseCase (private val saveContactRepository: SaveContactRepository) {
    suspend operator fun invoke(saveContactItem: SaveContactItem) {
        saveContactRepository.deleteSaveContactItem(saveContactItem)
    }
}