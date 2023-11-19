package solutions.mobiledev.reminder.presentation.widget.calendarTwo

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import solutions.mobiledev.reminder.core.services.BaseBroadcast
import org.koin.core.component.KoinComponent

class CalendarTwoNextReceiver : BaseBroadcast(), KoinComponent {


    override fun onReceive(context: Context?, intent: Intent?) {



            if (intent != null && ACTION_NEXT == intent.action && context != null) {
                val action = intent.getIntExtra(ARG_VALUE, 0)
                val widgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                val prefsProviderTwo = CalendarTwoWidgetPrefsProvider(context, widgetId)
                var month = prefsProviderTwo.getMonth()
                var year = prefsProviderTwo.getYear()
                if (action != 0) {
                    if (month in 0..10) {
                        month += 1
                    } else {
                        month = 0
                    }
                    prefsProviderTwo.setMonth(month)
                    if (month == 0) {
                        year += 1
                    }
                    prefsProviderTwo.setYear(year)
                    updatesHelper.updateCalendarTwoWidget()
                }
            }
        }


    companion object {
        const val ACTION_NEXT = "solutions.mobiledev.reminder.presentation.widget.calendarTwo.ACTION_NEXT"
        const val ARG_VALUE = "action_value"

    }
}