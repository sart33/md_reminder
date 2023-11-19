package solutions.mobiledev.reminder.domain.reminder.melody

import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.repository.FileRepository
import solutions.mobiledev.reminder.domain.repository.MelodyRepository

class GetMelodyItemUseCase(private val melodyRepository: MelodyRepository) {

    suspend operator fun invoke(melodyItemId: Int): MelodyItem {
        return melodyRepository.getMelodyItem(melodyItemId)
    }
}