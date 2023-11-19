package solutions.mobiledev.reminder.domain.reminder.image

import solutions.mobiledev.reminder.domain.repository.ImageRepository

class BindCurrentImageToReminderUseCase (private val imageRepository: ImageRepository) {

    suspend operator fun invoke(remId: Int) {
        imageRepository.bindCurrentImagesToReminder(remId)
    }
}