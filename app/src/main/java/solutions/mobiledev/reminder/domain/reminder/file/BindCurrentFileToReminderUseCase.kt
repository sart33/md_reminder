package solutions.mobiledev.reminder.domain.reminder.file

import solutions.mobiledev.reminder.domain.repository.FileRepository
import solutions.mobiledev.reminder.domain.repository.ImageRepository

class BindCurrentFileToReminderUseCase (private val fileRepository: FileRepository) {

    suspend operator fun invoke(remId: Int) {
        fileRepository.bindCurrentFilesToReminder(remId)
    }
}