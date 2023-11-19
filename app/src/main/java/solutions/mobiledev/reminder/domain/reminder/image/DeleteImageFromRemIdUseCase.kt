package solutions.mobiledev.reminder.domain.reminder.image

import solutions.mobiledev.reminder.domain.repository.ImageRepository

class DeleteImageFromRemIdUseCase(private val imageRepository: ImageRepository) {

    suspend operator fun invoke(remId: Int) {
        imageRepository.deleteImageFromRemId(remId)
    }
}