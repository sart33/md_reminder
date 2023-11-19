package solutions.mobiledev.reminder.domain.reminder

import solutions.mobiledev.reminder.domain.repository.ReminderRepository

class TimeActivateReminderTimerUseCase(
    private val reminderRepository: ReminderRepository

    ) {
    operator fun invoke(time: String, eventId: Int) {
        return reminderRepository.timeActivateRemindTimer(time, eventId)
    }
}