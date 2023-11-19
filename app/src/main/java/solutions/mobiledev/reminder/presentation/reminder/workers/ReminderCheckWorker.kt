package solutions.mobiledev.reminder.presentation.reminder.workers

import android.app.Application
import android.app.Notification
import android.content.Context
import androidx.work.*
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.data.reminder.ReminderListMapper
import solutions.mobiledev.reminder.data.reminder.ReminderRepositoryImpl
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.reminder.EditDateTimeRemindingAndCountUseCase
import solutions.mobiledev.reminder.presentation.reminder.workers.TimerWorker.Companion.NOTIFICATION_ID
import java.time.LocalDateTime.now
import java.util.concurrent.TimeUnit

class ReminderCheckWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val remRepository = ReminderRepositoryImpl(context as Application)
    val application = context.applicationContext as Application
    private val editDateTimeRemindingAndCount = EditDateTimeRemindingAndCountUseCase(remRepository)
    private val reminderListDao = AppDatabase.getInstance(context as Application).reminderListDao()
    private val mapper = ReminderListMapper()
    private lateinit var reminderItem: ReminderItem


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID, createNotification()
        )
    }

    private fun createNotification(): Notification {
        TODO()
    }

    override suspend fun doWork(): Result {

        val queryRes = reminderListDao.timeActivateRemind(
            now().toString(),
            now().plusMinutes(15).toString()
        )

        if (!queryRes.isNullOrEmpty()) {
            queryRes.forEach { it ->
                reminderItem = mapper.mapDbModelToEntity(it)

                val count = reminderItem.menuRepeatCount
                val frequency = reminderItem.menuRepeatFrequency
                val eventId = reminderItem.id
                val state = reminderItem.state

                if (count > 0 && state) {
                    val timerWorkerRequest = TimerWorker.makeRequest(
                        reminderItem.firstDateTimeReminding.toString(),
                        eventId
                    )
                    WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                        eventId.toString(),
                        ExistingWorkPolicy.REPLACE,
                        timerWorkerRequest
                    )

                    val nextDateTimeReminding =
                        (reminderItem.firstDateTimeReminding.toLocalTime().plusMinutes(
                            frequency.toLong()
                        )).atDate(reminderItem.firstDateTimeReminding.toLocalDate())
                    val newCount = count - 1
                    if (newCount > 0) {
                        editDateTimeRemindingAndCount.invoke(
                            eventId,
                            nextDateTimeReminding.toString(),
                            newCount
                        )
                    }
                }
            }
        }
        return Result.success()
    }


    companion object {

        const val NAME = "ReminderCheckWorker"
        const val TIME_TO_EVENT = "timeToEvent"
        private const val REMIND_ID = "remindId"
        private const val REMINDER_CHECK_WORKER = "reminder_check_worker"

        fun createPeriodicWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiresDeviceIdle(false) // Отключение требования простоя устройства
                .setRequiresBatteryNotLow(false) // Отключение требования низкого заряда батареи
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresStorageNotLow(false)
                .build()
            return PeriodicWorkRequestBuilder<ReminderCheckWorker>(
                15,
                TimeUnit.MINUTES,
                15,
                TimeUnit.MINUTES // Интервал повторения ReminderCheckWorker
            )
                .setConstraints(constraints)
                .build()
        }
    }
}