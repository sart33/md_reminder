package solutions.mobiledev.reminder.presentation.reminder.fragment

import androidx.recyclerview.widget.ListAdapter
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.presentation.CurrentStateHolder
import solutions.mobiledev.reminder.presentation.reminder.ReminderItemDiffCallback
import solutions.mobiledev.reminder.presentation.reminder.ReminderItemViewHolder
import solutions.mobiledev.reminder.presentation.settings.DateTimeManager
import solutions.mobiledev.reminder.presentation.widget.UpdatesHelper
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


abstract class DateTimeListAdapter() : ListAdapter<ReminderItem, ReminderItemViewHolder>(
    ReminderItemDiffCallback()), KoinComponent {
    protected val prefs by inject<Prefs>()
    protected val language by inject<Language>()
    protected val dateTimeManager by inject<DateTimeManager>()
}