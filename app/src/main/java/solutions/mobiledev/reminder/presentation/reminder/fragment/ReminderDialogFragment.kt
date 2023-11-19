package solutions.mobiledev.reminder.presentation.reminder.fragment

import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.presentation.settings.DateTimeManager
import org.koin.core.component.KoinComponent


abstract class ReminderDialogFragment() : DialogFragment(), KoinComponent {
    protected val prefs by inject<Prefs>()
    protected val language by inject<Language>()
    protected val dateTimeManager by inject<DateTimeManager>()
}