package solutions.mobiledev.reminder.data.reminder.addImage

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class ImageRepositoryImpl(application: Application) : ImageRepository {
    private val imageListDao = AppDatabase.getInstance(application).imageListDao()
    private val mapper = ImageListMapper()

    override suspend fun getImageItem(imageItemId: Int): ImageItem {
        return mapper.mapDbModelToEntity(imageListDao.getImageItem(imageItemId))
    }

    override fun getImageWithRemId(remId: Int): LiveData<List<ImageItem>> =
        MediatorLiveData<List<ImageItem>>()
            .apply {
                addSource(imageListDao.getImageWithRemId(remId)) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }

    override suspend fun addImageItem(imageItem: ImageItem) {
        imageListDao.addImageItem(mapper.mapEntityToDbModel(imageItem))
    }

    override suspend fun editImageItem(imageItem: ImageItem) {
        imageListDao.addImageItem(mapper.mapEntityToDbModel(imageItem))
    }

    override suspend fun deleteImageItem(imageItem: ImageItem) {
        imageListDao.deleteImageItem(imageItem.id)
    }

    override suspend fun bindCurrentImagesToReminder(remId: Int) {
        imageListDao.bindCurrentImagesToReminder(remId)
    }

    override suspend fun deleteImageFromRemId(remId: Int) {
        imageListDao.deleteImageFromRemId(remId)
    }


}