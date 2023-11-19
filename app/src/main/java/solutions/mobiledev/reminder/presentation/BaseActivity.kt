package solutions.mobiledev.reminder.presentation

import androidx.appcompat.app.AppCompatActivity
import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.presentation.settings.DateTimeManager
import solutions.mobiledev.reminder.presentation.widget.UpdatesHelper
import org.koin.android.ext.android.inject
import org.koin.core.Koin

abstract class BaseActivity: AppCompatActivity() {
    protected val updatesHelper by inject<UpdatesHelper>()
    protected val currentStateHolder by inject<CurrentStateHolder>()
    protected val prefs = currentStateHolder.preferences
    protected val language by inject<Language>()
    protected val dateTimeManager by inject<DateTimeManager>()

}