package solutions.mobiledev.reminder

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import solutions.mobiledev.reminder.presentation.reminder.workers.ReminderCheckWorker
import solutions.mobiledev.reminder.presentation.utilModule
import solutions.mobiledev.reminder.presentation.workerModule

class ReminderApp : Application(), KoinComponent {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }


    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        // Создайте периодический запрос для ReminderCheckWorker
        val periodicWorkRequest = ReminderCheckWorker.createPeriodicWorkRequest()

        // Запланируйте периодический запуск ReminderCheckWorker
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            REMINDER_CHECK_WORKER,
            ExistingPeriodicWorkPolicy.UPDATE, // Что делать, если такой запрос уже запланирован
            periodicWorkRequest
        )


        val logger = object : Logger(level = Level.DEBUG) {
            override fun display(level: Level, msg: MESSAGE) {
            }
        }
        startKoin {

            logger(logger)
            androidContext(this@ReminderApp)
            modules(
                listOf(
                    utilModule,
                    workerModule,
                )
            )
        }
    }




    override fun onTrimMemory(level: Int) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        Glide.get(this).clearMemory()
        super.onLowMemory()
    }

    companion object {
        private const val REMINDER_CHECK_WORKER = "reminder_check_worker"


    }
}