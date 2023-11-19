package solutions.mobiledev.reminder.presentation.reminder.workers

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.work.*
import kotlinx.coroutines.*
import solutions.mobiledev.reminder.BuildConfig
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.data.reminder.ReminderRepositoryImpl
import solutions.mobiledev.reminder.domain.entity.FullReminderItem
import solutions.mobiledev.reminder.domain.reminder.GetReminderItemWithAllUseCase
import solutions.mobiledev.reminder.presentation.HomeStartNotificationActivity
import solutions.mobiledev.reminder.presentation.StartNotificationFragmentActivity
import solutions.mobiledev.reminder.presentation.notification.ReminderNotificationFragment
import solutions.mobiledev.reminder.presentation.widget.PrefsConstants.HOME_SCREEN_FULL
import solutions.mobiledev.reminder.presentation.widget.PrefsConstants.LOCK_SCREEN_FULL
import solutions.mobiledev.reminder.presentation.widget.PrefsConstants.PREFS_NAME
import solutions.mobiledev.reminder.presentation.widget.PrefsConstants.TURN_ON_SCREEN_WHEN_NOTIFICATION
import java.io.File
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class TimerWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,

    ) : CoroutineWorker(context, workerParams) {

    val prefs = Prefs(context)
    val application = context.applicationContext as Application
    private val remRepository = ReminderRepositoryImpl(application)
    val getReminderItemWithAll = GetReminderItemWithAllUseCase(remRepository)
    private lateinit var fullReminderItem: FullReminderItem
    private val notificationManager = NotificationManagerCompat.from(context)
    private val sharedPreferences: SharedPreferences =
        applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    var id = 0
    var remindId = 0
    var showOnce = false
    private lateinit var notificationChannel: NotificationChannel

    private var overlayView: View? = null

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID, createNotification()
        )
    }

    private fun createNotification(): Notification {
        TODO()
    }

    @SuppressLint("SuspiciousIndentation")
    override suspend fun doWork(): Result {


        val params = workerParams.inputData

        val firstRemTime = params.getString(FIRST_REMIND_TIME)
        remindId = params.getInt(REMIND_ID, 0)

        sharedPreferences.edit().putInt(REMIND_ID + "_" + remindId, remindId).apply()
        sharedPreferences.edit().putString(FIRST_REMIND_TIME + "_" + remindId, firstRemTime).apply()
        // Извлеките данные из SharedPreferences
        val idFromPref = sharedPreferences.getInt(REMIND_ID + "_" + remindId, 0)
        val remTimeFromPref = sharedPreferences.getString(FIRST_REMIND_TIME + "_" + remindId, "")

        getFullReminderViewItem(remindId)
        if (this@TimerWorker::fullReminderItem.isInitialized && remTimeFromPref != "" && fullReminderItem.state) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val localDateTime = LocalDateTime.parse(remTimeFromPref, formatter)
            val periodCycle = now().until(localDateTime, ChronoUnit.SECONDS)

            for (i in 0 until periodCycle) {
                delay(1000)
                if ((periodCycle - i).equals(0) && !showOnce) {
                    showOnce = true
                    createNotification2()
                    sharedPreferences.edit().remove(FIRST_REMIND_TIME + "_" + remindId).apply()
                    sharedPreferences.edit().remove(REMIND_ID + "_" + remindId).apply()
                }
            }
        } else {
//            return Result.failure()
        }
        if (idFromPref != 0 && idFromPref == remindId) {
            createNotification2()
            sharedPreferences.edit().remove(FIRST_REMIND_TIME + "_" + remindId).apply()
            sharedPreferences.edit().remove(REMIND_ID + "_" + remindId).apply()

        }
        return Result.success()
    }

    private suspend fun getFullReminderViewItem(reminderId: Int) {
        coroutineScope {
            fullReminderItem = getReminderItemWithAll(reminderId)
        }
    }


    private fun createNotification2(): Unit {
        if (fullReminderItem.state) {
            val contentIntent =
                Intent(context, StartNotificationFragmentActivity::class.java).apply {
                    putExtra(FRAGMENT, ReminderNotificationFragment::class.java.name)
                    putExtra(REMINDER_ITEM_ID, remindId.toString())
                }
            val contentIntentHome = Intent(context, HomeStartNotificationActivity::class.java).apply {
                putExtra(FRAGMENT, ReminderNotificationFragment::class.java.name)
                putExtra(REMINDER_ITEM_ID, remindId.toString())
            }

            val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getActivity(
                    context, 0, contentIntent, FLAG_MUTABLE or FLAG_UPDATE_CURRENT
                )
            } else {
                getActivity(
                    context, 0, contentIntent, FLAG_UPDATE_CURRENT
                )

            }

            val contentPendingIntentHome = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getActivity(
                    context, 0, contentIntentHome, FLAG_MUTABLE or FLAG_UPDATE_CURRENT
                )
            } else {
                getActivity(
                    context, 0, contentIntentHome, FLAG_UPDATE_CURRENT
                )

            }

            // Создание объекта Bitmap изображения
            val bitmap = BitmapFactory.decodeResource(
                applicationContext.resources, R.drawable.notification
            )

            // Создание объекта BigPictureStyle
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(bitmap) // Установите большую иконку, если нужно, иначе передайте null
                .setSummaryText(fullReminderItem.body)

            // Создание уведомления с использованием BigPictureStyle
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            val homeScreenFull = sharedPreferences.getInt(HOME_SCREEN_FULL, 1)
            val lockScreenFull = sharedPreferences.getInt(LOCK_SCREEN_FULL, 1)
            val turnOnScreen = sharedPreferences.getInt(TURN_ON_SCREEN_WHEN_NOTIFICATION, 1)


            /**
             * Создание канала уведомлений (требуется начиная с Android 8.0)
             */
            if (turnOnScreen == 0) {
                notificationChannel = NotificationChannel(
                    CHANNEL_ID + "_" + remindId,
                    CHANNEL_NAME + "_" + remindId,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    lockscreenVisibility = VISIBILITY_PUBLIC
                }
            } else {
                notificationChannel = NotificationChannel(
                    CHANNEL_ID + "_" + remindId,
                    CHANNEL_NAME + "_" + remindId,
                    NotificationManager.IMPORTANCE_HIGH
                )
            }


            val audioAttributes =
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            if (!fullReminderItem.melodyName.isNullOrBlank()) {
                val soundFile = File(
                    context.getExternalFilesDir("Music"), fullReminderItem.melodyName.toString()
                )
                Log.d("Notification Worker", "fullReminderItem.melodyName: ${fullReminderItem.melodyName}")

                val soundUri = FileProvider.getUriForFile(
                    context, "${BuildConfig.APPLICATION_ID}.provider", soundFile
                )
                notificationChannel.setSound(soundUri, audioAttributes)
            } else {
                if ((prefs.notificationDefaultSound).isEmpty()) {

                } else {
                    val fileName = prefs.notificationDefaultSound
                    Log.d("Notification Worker", "prefs.notificationDefaultSound: ${prefs.notificationDefaultSound}")
                    val soundFile = File(
                        context.getExternalFilesDir("Music"), "default_$fileName"
                    )
                    val soundUri = FileProvider.getUriForFile(
                        context, "${BuildConfig.APPLICATION_ID}.provider", soundFile
                    )
                    notificationChannel.setSound(soundUri, audioAttributes)
                }
            }
            notificationChannel.setBypassDnd(true)
            notificationManager.createNotificationChannel(notificationChannel)
            /**
             * Создание уведомления
             */
            val notificationBuilder = NotificationCompat.Builder(
                context, CHANNEL_ID + "_" + remindId
            )
            notificationBuilder.setContentTitle("Напоминание $remindId")
            notificationBuilder.setContentText(fullReminderItem.body)
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
            notificationBuilder.setStyle(bigPictureStyle)
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
//            if (turnOnScreen == 0) {
//                // Установите видимость на экране блокировки
//                notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Установите видимость на экране блокировки
//                notificationBuilder.setCategory(NotificationCompat.CATEGORY_ALARM) // Установите категорию уведомления, если нужно
//            }
            if (homeScreenFull == 0) {
                notificationBuilder.setCategory(NotificationCompat.CATEGORY_ALARM)
                notificationBuilder.setFullScreenIntent(
                    contentPendingIntentHome,
                    true
                ) // Важно установить true для отображения на полный экран
                val handler = Handler(Looper.getMainLooper())
                // Выполнение фоновой задачи
                handler.post {
                    // Создание Intent для запуска активити
                    val intent = Intent(
                        applicationContext,
                        HomeStartNotificationActivity::class.java
                    ).apply {
                        putExtra(FRAGMENT, ReminderNotificationFragment::class.java.name)
                        putExtra(REMINDER_ITEM_ID, remindId.toString())
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    // Запуск активити в основном потоке
                    applicationContext.startActivity(intent)
                }
            }
                if (lockScreenFull == 0) {
                    notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Установите видимость на экране блокировки
                    notificationBuilder.setCategory(NotificationCompat.CATEGORY_ALARM)
                    notificationBuilder.setFullScreenIntent(
                        contentPendingIntent,
                        true
                    ) // Важно установить true для отображения на полный экран
                }
                if (homeScreenFull == 1 && lockScreenFull == 1) {
                    notificationBuilder.setContentIntent(contentPendingIntentHome)
                }


            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                notify(remindId, notificationBuilder.build())
            }
        }
    }




    companion object {

        private const val CHANNEL_ID = "channel_id"
        const val TIME_TO_EVENT = "timeToEvent"
        const val FIRST_REMIND_TIME = "firstRemindTime"
        private const val REMIND_ID = "remindId"
        private const val FRAGMENT = "fragment"
        private const val CHANNEL_NAME = "channel_name"
        private const val REMINDER_ITEM_ID = "reminder_item_id"

        const val NOTIFICATION_ID = 101
        const val NAME = "TimerWorker"


        fun makeRequest(firstRemTime: String, remindId: Int): OneTimeWorkRequest {
            val inputData =
                Data.Builder().putString(FIRST_REMIND_TIME, firstRemTime)
                    .putInt(REMIND_ID, remindId)
                    .build()
            return OneTimeWorkRequestBuilder<TimerWorker>().setInputData(inputData).build()

        }
    }
}