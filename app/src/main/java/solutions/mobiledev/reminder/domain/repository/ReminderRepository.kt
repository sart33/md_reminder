package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkRequest
import solutions.mobiledev.reminder.data.reminder.DateDiapason
import solutions.mobiledev.reminder.data.reminder.FullReminderItemDbModel
import solutions.mobiledev.reminder.data.reminder.ReminderItemDbModel
import solutions.mobiledev.reminder.data.reminder.ReminderItemWithMelody
import solutions.mobiledev.reminder.domain.entity.FullReminderItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem

interface ReminderRepository {

    fun getReminderList(): LiveData<List<ReminderItem>>

    fun getSomeNextReminders(dateNow: String): List<ReminderItem>

    fun getRemindersAtDate(date: String): LiveData<List<ReminderItem>>

    suspend fun getReminderItem(reminderId: Int): ReminderItem

    suspend fun addReminderItem(reminderItem: ReminderItem)

    suspend fun editReminderItem(reminderItem: ReminderItem)

    suspend fun deleteReminderItem(reminderItem: ReminderItem)

    suspend fun activeChangeStatus(reminderId: Int, status: Boolean)

    suspend fun showInWidgetChangeStatus(reminderId: Int, status: Boolean)

    suspend fun editDateTimeReminding(reminderId: Int, firstDateTimeReminding: String)

    suspend fun lastInsertValue(): Int

    suspend fun getRemindersCountAtDate(dateNow: String): Int

    suspend fun getRemindersCount(): Int

    suspend fun getRemindersCountAtDateDiapason(
        dateStart: String,
        dateEnd: String
    ): List<DateDiapason>

    suspend fun lastInsertTemporaryValue(): Int?

    suspend fun getReminderItemWithAll(reminderItemId: Int): FullReminderItem

    suspend fun editDateTimeRemindingAndCount(
        reminderId: Int,
        firstDateTimeReminding: String,
        count: Int
    )
    suspend fun editDateTimeDateTimeRemindingAndCount(
        reminderId: Int,
        firstDateTimeReminding: String,
        date: String,
        time: String,
        count: Int
    )
//    fun timeActivateRemindList()

    fun timeActivateRemindTimer(time: String, eventId: Int)


    //    fun workManagerChainParallel(workRequest1: WorkRequest, workRequest2: WorkRequest,
//                                 workRequest3: WorkRequest)
//    fun workManagerChainParallel(workRequest1: WorkRequest, workRequest2: WorkRequest)
    fun workManagerChainParallel(workRequestList: MutableList<WorkRequest>)

    fun checkDate()
}