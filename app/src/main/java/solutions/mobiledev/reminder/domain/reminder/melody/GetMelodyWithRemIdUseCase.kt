package solutions.mobiledev.reminder.domain.reminder.melody

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.repository.FileRepository
import solutions.mobiledev.reminder.domain.repository.MelodyRepository

class GetMelodyWithRemIdUseCase (private val melodyRepository: MelodyRepository) {

    operator fun invoke(remId: Int): LiveData<List<MelodyItem>> {
        return melodyRepository.getMelodyWithRemId(remId)
    }
}