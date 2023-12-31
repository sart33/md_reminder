package solutions.mobiledev.reminder.presentation.widget.calendar

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import solutions.mobiledev.reminder.core.services.BaseBroadcast

class CalendarPreviousReceiver : BaseBroadcast(), KoinComponent {


    override fun onReceive(context: Context?, intent: Intent?) {

            if (intent != null && ACTION_PREVIOUS == intent.action && context != null) {
                val action = intent.getIntExtra(ARG_VALUE, 0)
                val widgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                val prefsProvider = CalendarWidgetPrefsProvider(context, widgetId)
                var month = prefsProvider.getMonth()
                var year = prefsProvider.getYear()
                if (action != 0) {
                    if (month == 0) {
                        month = 11
                    } else {
                        month -= 1
                    }
                    prefsProvider.setMonth(month)
                    if (month == 11) {
                        year -= 1
                    }
                    prefsProvider.setYear(year)
                    updatesHelper.updateCalendarWidget()
                }
            }
        }

    companion object {
        const val ACTION_PREVIOUS = "solutions.mobiledev.reminder.presentation.widget.calendar.ACTION_PREVIOUS"
        const val ARG_VALUE = "action_value"
    }
}