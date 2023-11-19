package solutions.mobiledev.reminder.domain.reminder.file

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.repository.FileRepository
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class GetFileWithRemIdUseCase (private val fileRepository: FileRepository) {

    operator fun invoke(remId: Int): LiveData<List<FileItem>> {
        return fileRepository.getFileWithRemId(remId)
    }
}