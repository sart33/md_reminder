package solutions.mobiledev.reminder.presentation


import org.koin.dsl.module
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.TextProvider
import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.presentation.settings.DateTimeManager
import solutions.mobiledev.reminder.presentation.widget.UpdatesHelper


val utilModule = module {
        single { Prefs(get()) }
        single { Dialogues(get()) }
        single { Language(get(), get(), get()) }
        single { UpdatesHelper(get()) }
        single { TextProvider(get()) }
        single { CurrentStateHolder(get(), get(), get()) }
        single { DateTimeManager(get(), get(), get()) }

    }

val workerModule = module {
//        worker { ReminderCheckWorker(get(), get()) }
//        worker { TimerWorker(get(), get()) }
}
