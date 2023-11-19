package solutions.mobiledev.reminder.core.services

import android.content.BroadcastReceiver
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.presentation.widget.UpdatesHelper
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseBroadcast : BroadcastReceiver(), KoinComponent {
    protected val prefs by inject<Prefs>()
//    protected val notifier by inject<Notifier>()
    protected val updatesHelper by inject<UpdatesHelper>()
    protected val language by inject<Language>()



}