package solutions.mobiledev.reminder.domain.reminder.melody

import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.repository.FileRepository
import solutions.mobiledev.reminder.domain.repository.MelodyRepository

class AddMelodyItemUseCase(private val melodyRepository: MelodyRepository) {

    suspend operator fun invoke(melodyItem: MelodyItem) {
        melodyRepository.addMelodyItem(melodyItem)
    }
}