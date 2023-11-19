package solutions.mobiledev.reminder.domain.reminder.email

import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.repository.EmailRepository

class AddEmailItemUseCase(private val emailRepository: EmailRepository) {

    suspend operator fun invoke(emailItem: EmailItem) {
        emailRepository.addEmailItem(emailItem)
    }
}