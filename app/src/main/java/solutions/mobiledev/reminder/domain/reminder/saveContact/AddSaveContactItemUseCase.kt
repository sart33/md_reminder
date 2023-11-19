package solutions.mobiledev.reminder.domain.reminder.saveContact

import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.domain.repository.SaveContactRepository

class AddSaveContactItemUseCase(private val saveContactRepository: SaveContactRepository) {

    suspend operator fun invoke(saveContactItem: SaveContactItem) {
        saveContactRepository.addSaveContactItem(saveContactItem)
    }
}