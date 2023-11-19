package solutions.mobiledev.reminder.domain.reminder.file

import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.repository.FileRepository

class AddFileItemUseCase(private val fileRepository: FileRepository) {

    suspend operator fun invoke(fileItem: FileItem) {
        fileRepository.addFileItem(fileItem)
    }
}