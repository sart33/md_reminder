package solutions.mobiledev.reminder.presentation.widget.list

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.Actions
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.utils.WidgetUtils
import solutions.mobiledev.reminder.data.reminder.ReminderRepositoryImpl
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.reminder.GetSomeNextRemindersUseCase
import solutions.mobiledev.reminder.presentation.settings.DateTimeManager
import solutions.mobiledev.reminder.presentation.widget.Constants
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.CountDownLatch


class ReminderListAdapter(
    intent: Intent,
    private val context: Context,
    private val prefs: Prefs,
    private val dateTimeManager: DateTimeManager

) : RemoteViewsService.RemoteViewsFactory {

    val application = context.applicationContext as Application
    private val remRepository = ReminderRepositoryImpl(application)
    val getSomeNextReminders = GetSomeNextRemindersUseCase(remRepository)
    private var data: List<ReminderItem> = emptyList()
    private var isDataReady = false
    private val latch = CountDownLatch(1)

    private val widgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
    )
    private val prefsProvider = ReminderListPrefsProvider(context, widgetId)
    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            data = withContext(Dispatchers.IO) {
                return@withContext getSomeNextReminders(LocalDateTime.now().toString())
            }
            setData(data)
            isDataReady = true
            latch.countDown()
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        if (!isDataReady) {
            try {
                latch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return data.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val itemBgColor = prefsProvider.getItemBackground()
        // Устанавливаем цвет фона и текста в зависимости от цвета фона.
        val textColor = if (WidgetUtils.isDarkBg(itemBgColor)) {
            ContextCompat.getColor(context, R.color.pureWhite)
        } else {
            ContextCompat.getColor(context, R.color.pureBlack)
        }

        val views = RemoteViews(context.packageName, R.layout.list_item_widget)
        if (WidgetUtils.isDarkBg(itemBgColor)) {
            views.setInt(R.id.line_image_view_two, "setBackgroundResource", WidgetUtils.newWidgetBg(0))
        } else {
            views.setInt(R.id.line_image_view_two, "setBackgroundResource", WidgetUtils.newWidgetBg(21))
        }
        views.setInt(R.id.llWidgetBg, "setBackgroundResource", WidgetUtils.newWidgetBg(itemBgColor))
//        views.setInt(R.id.llWidgetBg, "setBackgroundResource", WidgetUtils.newWidgetBg(itemBgColor))


        views.setTextColor(R.id.tvWidgetDate, textColor)
        views.setTextColor(R.id.tvWidgetTime, textColor)
        views.setTextColor(R.id.tvWidgetMessage, textColor)
        val item = data.getOrNull(position)
        if (item != null) {

            val localDate = LocalDate.parse(item.dateRemaining)
            views.setTextViewText(R.id.tvWidgetDate, dateTimeManager.getDate(localDate))
            val localTime = LocalTime.parse(item.timeRemaining)
            views.setTextViewText(R.id.tvWidgetTime, dateTimeManager.getTime(localTime))
            views.setTextViewText(R.id.tvWidgetMessage, item.body)

            if (WidgetUtils.isDarkBg(itemBgColor)) {
                if (item.mails) views.setImageViewResource(
                    R.id.ivWidgetMailIcon,
                    R.drawable.widget_mail
                )
                if (item.contacts) views.setImageViewResource(
                    R.id.ivWidgetPhoneIcon, R.drawable.widget_phone
                )
                if (item.phoneNumbers) views.setImageViewResource(
                    R.id.ivWidgetPhoneIcon, R.drawable.widget_phone
                )
                if (item.sms) views.setImageViewResource(
                    R.id.ivWidgetSmsIcon,
                    R.drawable.widget_sms
                )
                if (item.file) views.setImageViewResource(
                    R.id.ivWidgetAttachFileIcon, R.drawable.widget_attachfile
                )
            } else {
                if (item.mails) views.setImageViewResource(
                    R.id.ivWidgetMailIcon, R.drawable.widget_mail_black
                )
                if (item.phoneNumbers) views.setImageViewResource(
                    R.id.ivWidgetPhoneIcon, R.drawable.widget_phone_black
                )
                if (item.contacts) views.setImageViewResource(
                    R.id.ivWidgetPhoneIcon, R.drawable.widget_phone_black
                )
                if (item.sms) views.setImageViewResource(
                    R.id.ivWidgetSmsIcon,
                    R.drawable.widget_sms_black
                )
                if (item.file) views.setImageViewResource(
                    R.id.ivWidgetAttachFileIcon, R.drawable.widget_attachfile_black
                )
            }

            val fillInIntent = Intent()
            fillInIntent.putExtra(Constants.INTENT_ID, item.id)
            fillInIntent.action = Actions.Reminder.ACTION_EDIT_REMIND
            fillInIntent.putExtra(ListActionReceiver.TYPE, true)
            views.setOnClickFillInIntent(R.id.tvWidgetDate, fillInIntent)
            views.setOnClickFillInIntent(R.id.tvWidgetTime, fillInIntent)
            views.setOnClickFillInIntent(R.id.tvWidgetMessage, fillInIntent)
            views.setOnClickFillInIntent(R.id.ivWidgetPhoneIcon, fillInIntent)
            views.setOnClickFillInIntent(R.id.ivWidgetSmsIcon, fillInIntent)
            views.setOnClickFillInIntent(R.id.ivWidgetAttachFileIcon, fillInIntent)
            views.setOnClickFillInIntent(R.id.widgetListItem, fillInIntent)


        }
        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        // Возвращает уникальный идентификатор элемента списка.
        return data.getOrNull(position)?.id?.toLong() ?: 0L

    }

    override fun hasStableIds(): Boolean {
        // Возвращает true, если уникальные идентификаторы элементов списка остаются неизменными.
        // Это нужно, чтобы списки в виджетах могли правильно обновляться при изменении данных.
        return true
    }

    private fun setData(data: List<ReminderItem>) {
        this.data = data
    }
}