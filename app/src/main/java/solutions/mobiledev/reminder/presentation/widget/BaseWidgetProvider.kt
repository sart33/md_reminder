package solutions.mobiledev.reminder.presentation.widget

import android.appwidget.AppWidgetProvider
import solutions.mobiledev.reminder.presentation.widget.calendar.CalendarWidgetPrefsProvider
import solutions.mobiledev.reminder.presentation.widget.calendarTwo.CalendarTwoWidgetPrefsProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseWidgetProvider() : AppWidgetProvider(), KoinComponent {
    protected val prefsProviderTwo by inject<CalendarTwoWidgetPrefsProvider>()
    protected val prefsProvider by inject<CalendarWidgetPrefsProvider>()

}