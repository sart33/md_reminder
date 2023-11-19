package solutions.mobiledev.reminder.core.utils

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.*

class LocaleManager(private val context: Context) {

    fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))

        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}