package solutions.mobiledev.reminder.presentation.widget

import android.appwidget.AppWidgetProvider
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.utils.Language
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class ReminderWidgetProvider() : AppWidgetProvider(), KoinComponent {
    protected val prefs by inject<Prefs>()
    //    protected val notifier by inject<Notifier>()
    protected val updatesHelper by inject<UpdatesHelper>()
    protected val language by inject<Language>()
}
