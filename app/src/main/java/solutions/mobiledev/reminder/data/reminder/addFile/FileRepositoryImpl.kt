package solutions.mobiledev.reminder.data.reminder.addFile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.repository.FileRepository

class FileRepositoryImpl(application: Application) : FileRepository {
    private val fileListDao = AppDatabase.getInstance(application).fileListDao()
    private val mapper = FileListMapper()

    override suspend fun getFileItem(fileItemId: Int): FileItem {
        return mapper.mapDbModelToEntity(fileListDao.getFileItem(fileItemId))
    }

    override fun getFileWithRemId(remId: Int): LiveData<List<FileItem>> =
        MediatorLiveData<List<FileItem>>()
            .apply {
                addSource(fileListDao.getFileWithRemId(remId)) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }

    override suspend fun addFileItem(fileItem: FileItem) {
        fileListDao.addFileItem(mapper.mapEntityToDbModel(fileItem))
    }

    override suspend fun editFileItem(fileItem: FileItem) {
        fileListDao.addFileItem(mapper.mapEntityToDbModel(fileItem))
    }

    override suspend fun deleteFileItem(fileItem: FileItem) {
        fileListDao.deleteFileItem(fileItem.id)
    }

    override suspend fun bindCurrentFilesToReminder(remId: Int) {
        fileListDao.bindCurrentFilesToReminder(remId)
    }

    override suspend fun deleteFileFromRemId(remId: Int) {
        fileListDao.deleteFileFromRemId(remId)
    }


}