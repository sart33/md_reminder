package solutions.mobiledev.reminder.domain.reminder.melody

import solutions.mobiledev.reminder.domain.repository.MelodyRepository

class BindCurrentMelodyToReminderUseCase (private val melodyRepository: MelodyRepository) {

    suspend operator fun invoke(remId: Int) {
        melodyRepository.bindCurrentMelodiesToReminder(remId)
    }
}