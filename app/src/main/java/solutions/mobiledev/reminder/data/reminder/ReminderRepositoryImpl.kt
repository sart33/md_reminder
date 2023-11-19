package solutions.mobiledev.reminder.data.reminder

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.FullReminderItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.ReminderRepository
import solutions.mobiledev.reminder.presentation.reminder.workers.TimerWorker

class ReminderRepositoryImpl(
    private val application: Application,
) : ReminderRepository {

    private var page = 0
    private val reminderListDao = AppDatabase.getInstance(application).reminderListDao()
    private val mapper = ReminderListMapper()

    override fun getReminderList(): LiveData<List<ReminderItem>> {
        return reminderListDao.getReminderList().map {
            mapper.mapListDbModelToListEntity(it)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun getSomeNextReminders(dateNow: String): List<ReminderItem> {
        val reminderList = this.reminderListDao.getSomeNextReminders(dateNow)
        return mapper.mapListDbModelToListEntity(reminderList)

    }

    override suspend fun getReminderItemWithAll(reminderItemId: Int): FullReminderItem {
        val dbModel = reminderListDao.getReminderItemWithAll(reminderItemId)
        return mapper.mapDbModelWithMelodyToEntity(dbModel)
    }

    override fun getRemindersAtDate(date: String): LiveData<List<ReminderItem>> =
        reminderListDao.getRemindersAtDate(date).map {
            mapper.mapListDbModelToListEntity(it)
        }

    //    override fun timeActivateRemindList() {
//        val workManager = WorkManager.getInstance(application)
//        workManager.enqueueUniquePeriodicWork(
//            ReminderCheckWorker.NAME,
//            ExistingPeriodicWorkPolicy.UPDATE,
//            ReminderCheckWorker.makeRequest()
//        )
//    }
    override fun timeActivateRemindTimer(time: String, eventId: Int) {
        val workManager = WorkManager.getInstance(application)
        workManager.enqueueUniqueWork(
            TimerWorker.NAME,
            ExistingWorkPolicy.REPLACE,
            TimerWorker.makeRequest(time, eventId)
        )
    }

//  override fun  workManagerChainParallel(myWorkRequest1: WorkRequest, myWorkRequest2: WorkRequest,
//                                myWorkRequest3: WorkRequest) {
//        //Параллельный запуск 3 задач
//        WorkManager.getInstance(application)
//            .enqueue(myWorkRequest1, myWorkRequest2, myWorkRequest3)
//    }
//
//  override fun  workManagerChainParallel(myWorkRequest1: WorkRequest, myWorkRequest2: WorkRequest) {
//        //Параллельный запуск 2 задач
//        WorkManager.getInstance(application)
//            .enqueue(myWorkRequest1, myWorkRequest2)
//    }

    override fun workManagerChainParallel(workRequestList: MutableList<WorkRequest>) {

        WorkManager.getInstance(application)
            .enqueue(workRequestList)
    }

    override fun checkDate() {
//        val workManager = WorkManager.getInstance(application)
//        workManager.enqueueUniqueWork(
//            RefreshReminderWorker.NAME,
//            ExistingWorkPolicy.REPLACE,
//            RefreshReminderWorker.makeRequest(page++)
//        )
    }

    override suspend fun getReminderItem(reminderId: Int): ReminderItem {
        val dbModel = reminderListDao.getReminderItem(reminderId)
        return mapper.mapDbModelToEntity(dbModel)
    }


    override suspend fun addReminderItem(reminderItem: ReminderItem) {
        reminderListDao.addReminderItem(mapper.mapEntityToDbModel(reminderItem))

    }


    override suspend fun editReminderItem(reminderItem: ReminderItem) {
        reminderListDao.addReminderItem(mapper.mapEntityToDbModel(reminderItem))
    }


    override suspend fun editDateTimeRemindingAndCount(
        reminderId: Int,
        firstDateTimeReminding: String, count: Int
    ) {
        reminderListDao.editDateTimeRemindingAndCount(reminderId, firstDateTimeReminding, count)


    }


    override suspend fun editDateTimeDateTimeRemindingAndCount(
        reminderId: Int,
        firstDateTimeReminding: String, date: String, time: String, count: Int
    ) {
        reminderListDao.editDateTimeDateTimeRemindingAndCount(
            reminderId,
            firstDateTimeReminding,
            date,
            time,
            count
        )


    }

    override suspend fun deleteReminderItem(reminderItem: ReminderItem) {
        reminderListDao.deleteReminderItem(reminderItem.id)
    }

    override suspend fun activeChangeStatus(reminderId: Int, status: Boolean) {
        reminderListDao.activeChangeStatus(reminderId, status)
    }

    override suspend fun showInWidgetChangeStatus(reminderId: Int, status: Boolean) {
        reminderListDao.showInWidgetChangeStatus(reminderId, status)

    }

    override suspend fun lastInsertValue(): Int {
        return reminderListDao.lastInsertId()
    }

    override suspend fun getRemindersCount(): Int {
        return reminderListDao.getRemindersCount()
    }

    override suspend fun getRemindersCountAtDate(dateNow: String): Int {
        return reminderListDao.getRemindersCountAtDate(dateNow)
    }

    override suspend fun getRemindersCountAtDateDiapason(
        dateStart: String,
        dateEnd: String
    ): List<DateDiapason> {
        return reminderListDao.getRemindersCountAtDateDiapason(dateStart, dateEnd)
    }

    override suspend fun lastInsertTemporaryValue(): Int? {
        return reminderListDao.lastInsertIdWithTemporary()
    }

    override suspend fun editDateTimeReminding(reminderId: Int, firstDateTimeReminding: String) {
        reminderListDao.editDateTimeReminding(reminderId, firstDateTimeReminding)
    }


}