package solutions.mobiledev.reminder.domain.reminder.saveContact

import solutions.mobiledev.reminder.domain.repository.EmailRepository
import solutions.mobiledev.reminder.domain.repository.SaveContactRepository

class BindCurrentSaveContactToReminderUseCase (private val saveContactRepository: SaveContactRepository) {

    suspend operator fun invoke(remId: Int) {
        saveContactRepository.bindCurrentSaveContactToReminder(remId)
    }
}