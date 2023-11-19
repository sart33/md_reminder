package solutions.mobiledev.reminder.domain.reminder.melody

import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.repository.MelodyRepository

class GetMelodyUseRemIdUseCase(private val melodyRepository: MelodyRepository) {

    suspend operator fun invoke(remId: Int): MelodyItem {
        return melodyRepository.getMelodyUseRemId(remId)
    }
}