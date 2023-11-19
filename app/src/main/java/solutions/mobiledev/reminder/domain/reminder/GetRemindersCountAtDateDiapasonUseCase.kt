package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.data.reminder.DateDiapason
import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class GetRemindersCountAtDateDiapasonUseCase(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(dateStart: String, dateEnd: String) : List<DateDiapason> {
      return  reminderRepository.getRemindersCountAtDateDiapason(dateStart, dateEnd)
    }
}