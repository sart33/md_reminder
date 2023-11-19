package solutions.mobiledev.reminder.presentation

import android.content.Context
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.utils.Language


class CurrentStateHolder(
    prefs: Prefs,
    val language: Language,
    val context: Context,

) {
    val preferences = prefs
}