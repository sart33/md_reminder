package solutions.mobiledev.reminder.domain.reminder.melody

import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.repository.MelodyRepository

class DeleteMelodyItemUseCase (private val melodyRepository: MelodyRepository) {

    suspend operator fun invoke(melodyItem: MelodyItem) {
        melodyRepository.deleteMelodyItem(melodyItem)
    }
}