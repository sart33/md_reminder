package solutions.mobiledev.reminder.domain.reminder.menu

import solutions.mobiledev.reminder.domain.entity.ReminderMenuItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.ReminderMenuRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class AddReminderMenuItemUseCase (private val reminderMenuRepository: ReminderMenuRepository) {

    suspend operator fun invoke(reminderMenuItem: ReminderMenuItem) {
        reminderMenuRepository.addReminderMenuItem(reminderMenuItem)
    }
}