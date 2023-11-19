package solutions.mobiledev.reminder.domain.reminder.email

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.repository.EmailRepository

class GetEmailWithRemIdUseCase (private val emailRepository: EmailRepository) {

    operator fun invoke(remId: Int): LiveData<List<EmailItem>> {
        return emailRepository.getEmailWithRemId(remId)
    }
}