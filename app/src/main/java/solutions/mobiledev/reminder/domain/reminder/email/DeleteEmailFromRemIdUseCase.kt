package solutions.mobiledev.reminder.domain.reminder.email

import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.repository.EmailRepository

class DeleteEmailFromRemIdUseCase(private val emailRepository: EmailRepository) {

    suspend operator fun invoke(remId: Int) {
        emailRepository.deleteEmailFromRemId(remId)
    }
}