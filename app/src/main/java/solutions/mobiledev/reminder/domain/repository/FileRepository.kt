package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.ImageItem

interface FileRepository {

    fun getFileWithRemId(remId: Int): LiveData<List<FileItem>>

    suspend fun getFileItem(fileItemId: Int): FileItem

    suspend fun addFileItem(fileItem: FileItem)

    suspend fun editFileItem(fileItem: FileItem)

    suspend fun deleteFileItem(fileItem: FileItem)

    suspend fun deleteFileFromRemId(remId: Int)

    suspend fun bindCurrentFilesToReminder(remId: Int)
}