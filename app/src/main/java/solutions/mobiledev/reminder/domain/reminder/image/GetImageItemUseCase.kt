package solutions.mobiledev.reminder.domain.reminder.image

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class GetImageItemUseCase(private val imageRepository: ImageRepository) {

    suspend operator fun invoke(imageItemId: Int): ImageItem {
        return imageRepository.getImageItem(imageItemId)
    }
}