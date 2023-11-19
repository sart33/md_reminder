package solutions.mobiledev.reminder.domain.reminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetRemindersCountUseCase (private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(): Int {
        return reminderRepository.getRemindersCount()
    }
}