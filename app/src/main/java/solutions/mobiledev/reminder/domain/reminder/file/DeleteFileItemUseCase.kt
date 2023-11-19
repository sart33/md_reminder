package solutions.mobiledev.reminder.domain.reminder.file

import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.repository.FileRepository
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class DeleteFileItemUseCase (private val fileRepository: FileRepository) {

    suspend operator fun invoke(fileItem: FileItem) {
        fileRepository.deleteFileItem(fileItem)
    }
}