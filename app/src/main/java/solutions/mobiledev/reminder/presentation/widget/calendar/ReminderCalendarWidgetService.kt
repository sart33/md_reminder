package solutions.mobiledev.reminder.presentation.widget.calendar

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.widget.RemoteViewsService
import org.koin.android.ext.android.get



class ReminderCalendarWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CalendarWidgetFactory(intent, applicationContext, appWidgetManager, appWidgetIds, get(), get())
    }

    companion object {
        lateinit var appWidgetManager: AppWidgetManager
        lateinit var appWidgetIds: IntArray
    }

    override fun onCreate() {
        super.onCreate()
        // Получаем информацию о виджетах
        val widgetIds = AppWidgetManager.getInstance(this@ReminderCalendarWidgetService)
            .getAppWidgetIds(ComponentName(this@ReminderCalendarWidgetService, ReminderCalendarWidget::class.java))
        // Инициализируем переменные
        appWidgetIds = widgetIds
        appWidgetManager = AppWidgetManager.getInstance(this@ReminderCalendarWidgetService)
    }
}