package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ImageItem

interface ImageRepository {

    fun getImageWithRemId(remId: Int): LiveData<List<ImageItem>>

    suspend fun getImageItem(imageItemId: Int): ImageItem

    suspend fun addImageItem(imageItem: ImageItem)

    suspend fun editImageItem(imageItem: ImageItem)

    suspend fun deleteImageItem(imageItem: ImageItem)

    suspend fun deleteImageFromRemId(remId: Int)

    suspend fun bindCurrentImagesToReminder(remId: Int)
}