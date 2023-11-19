package solutions.mobiledev.reminder.domain.reminder.menu

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ReminderMenuItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.ReminderMenuRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class GetReminderMenuWithRemIdUseCase(private val reminderMenuRepository: ReminderMenuRepository) {

    operator fun invoke(remId: Int): LiveData<List<ReminderMenuItem>> {
        return reminderMenuRepository.getReminderMenuWithRemId(remId)
    }
}