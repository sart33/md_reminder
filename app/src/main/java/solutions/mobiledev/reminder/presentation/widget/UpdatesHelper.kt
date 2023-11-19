package solutions.mobiledev.reminder.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import solutions.mobiledev.reminder.presentation.widget.calendar.ReminderCalendarWidget
import solutions.mobiledev.reminder.presentation.widget.calendarTwo.ReminderCalendarTwoWidget
import solutions.mobiledev.reminder.presentation.widget.list.ReminderListWidget

class UpdatesHelper(
    private val context: Context
) {

    fun updateWidgets() {
        val intent = Intent(context, ReminderListWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, ReminderListWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
        updateCalendarWidget()
        updateCalendarTwoWidget()

    }

    fun updateCalendarTwoWidget() {
        val intent = Intent(context, ReminderCalendarTwoWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, ReminderCalendarTwoWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    fun updateCalendarWidget() {
        val intent = Intent(context, ReminderCalendarWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, ReminderCalendarWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

}