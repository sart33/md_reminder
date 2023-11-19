package solutions.mobiledev.reminder.domain.reminder.image

import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class DeleteEmailItemUseCase (private val imageRepository: ImageRepository) {

    suspend operator fun invoke(imageItem: ImageItem) {
        imageRepository.deleteImageItem(imageItem)
    }
}