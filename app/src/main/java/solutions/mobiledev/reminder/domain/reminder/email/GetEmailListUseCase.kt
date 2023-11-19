package solutions.mobiledev.reminder.domain.reminder.email

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.repository.EmailRepository

class GetEmailListUseCase (private val emailRepository: EmailRepository) {

    operator fun invoke(): LiveData<List<EmailItem>> {
        return emailRepository.getEmailList()
    }
}