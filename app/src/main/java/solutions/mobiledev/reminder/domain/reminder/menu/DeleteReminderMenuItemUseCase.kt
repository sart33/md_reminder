package solutions.mobiledev.reminder.domain.reminder.menu


import solutions.mobiledev.reminder.domain.entity.ReminderMenuItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.ReminderMenuRepository
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class DeleteReminderMenuItemUseCase (private val reminderMenuRepository: ReminderMenuRepository) {
    suspend operator fun invoke(reminderMenuItem: ReminderMenuItem) {
        reminderMenuRepository.deleteReminderMenuItem(reminderMenuItem)
    }
}