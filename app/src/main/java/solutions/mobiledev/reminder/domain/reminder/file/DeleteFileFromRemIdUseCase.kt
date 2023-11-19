package solutions.mobiledev.reminder.domain.reminder.file

import solutions.mobiledev.reminder.domain.repository.FileRepository
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class DeleteFileFromRemIdUseCase(private val fileRepository: FileRepository) {

    suspend operator fun invoke(remId: Int) {
        fileRepository.deleteFileFromRemId(remId)
    }
}