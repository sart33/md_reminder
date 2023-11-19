package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.ReminderMenuItem

interface ReminderMenuRepository {

    fun getReminderMenuList(): LiveData<List<ReminderMenuItem>>

    fun getReminderMenuWithRemId(remId: Int): LiveData<List<ReminderMenuItem>>

    suspend fun getReminderMenuItem(reminderMenuItemId: Int): ReminderMenuItem

    suspend fun addReminderMenuItem( reminderMenuItem: ReminderMenuItem)

    suspend fun editReminderMenuItem(reminderMenuItem: ReminderMenuItem)

    suspend fun deleteReminderMenuItem(reminderMenuItem: ReminderMenuItem)

    suspend fun deleteReminderMenuFromRemId(remId: Int)

    suspend fun bindCurrentReminderMenuToReminder(remId: Int)
}