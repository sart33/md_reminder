package solutions.mobiledev.reminder.domain.reminder.image

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class GetImageWithRemIdUseCase (private val imageRepository: ImageRepository) {

    operator fun invoke(remId: Int): LiveData<List<ImageItem>> {
        return imageRepository.getImageWithRemId(remId)
    }
}