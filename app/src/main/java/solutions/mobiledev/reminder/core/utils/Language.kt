package solutions.mobiledev.reminder.core.utils

import android.content.Context
import android.content.res.Configuration
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.TextProvider
import java.util.*

class Language (private val prefs: Prefs,
                private val context: Context,
                private val textProvider: TextProvider
)
{ /**
 * Holder locale for tts.
 *
 * @param isBirth flag for birthdays.
 * @return Locale
 */
    fun getLocale(appLanguage: Int): Locale? {
        var res: Locale? = null
        when (
            prefs.ttsLocale
        ) {
            ENGLISH -> res = Locale.ENGLISH
            FRENCH -> res = Locale.FRENCH
            GERMAN -> res = Locale.GERMAN
            JAPANESE -> res = Locale.JAPANESE
            ITALIAN -> res = Locale.ITALIAN
            KOREAN -> res = Locale.KOREAN
            POLISH -> res = Locale("pl", "")
            RUSSIAN -> res = Locale("ru", "")
            SPANISH -> res = Locale("es", "")
            UKRAINIAN -> res = Locale("uk", "")
            PORTUGUESE -> res = Locale("pt", "")
            SWEDISH -> res = Locale("sv", "")

        }
        return res
    }

    fun onAttach(context: Context): Context {
        return setLocale(context, getScreenLanguage(prefs.appLanguage)).also {
            textProvider.updateContext(it)
        }
    }

    private fun setLocale(context: Context, locale: Locale): Context {
        return updateResources(context, locale)
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    fun getLanguages(context: Context) = listOf(
        context.getString(R.string.english),
        context.getString(R.string.ukrainian),
        context.getString(R.string.spanish),
//        context.getString(R.string.portuguese),
//        context.getString(R.string.polish),
//        context.getString(R.string.italian)
    )

    fun getTextLanguage(code: Int) = when (code) {
        0 -> ENGLISH
        1 -> SPANISH
        2 -> FRENCH
        3 -> GERMAN
        4 -> SWEDISH
        5 -> UKRAINIAN
        6 -> RUSSIAN
        else -> ENGLISH
    }

    fun getLanguage(code: Int) = when (code) {
        0 -> EN
        1 -> ES
        2 -> FR
        3 -> DE
        4 -> SV
        5 -> UK
        6 -> RU
        else -> EN
    }

    fun getVoiceLocale(code: Int): Locale = when (code) {
        0 -> Locale.ENGLISH
        1 -> Locale("uk", "")
        2 -> Locale("es", "")
        3 -> Locale("pt", "")
        4 -> Locale("pl", "")
        5 -> Locale.ITALIAN
        else -> Locale.ENGLISH
    }


    fun getLocaleByPosition(position: Int) = when (position) {
        0 -> ENGLISH
        1 -> SPANISH
        2 -> FRENCH
        3 -> GERMAN
        4 -> SWEDISH
        5 -> UKRAINIAN
        6 -> RUSSIAN
        else -> ENGLISH
    }

    fun getLocalePosition(locale: String?) = when {
        locale == null -> 0
        locale.matches(ENGLISH.toRegex()) -> 0
        locale.matches(SPANISH.toRegex()) -> 1
        locale.matches(FRENCH.toRegex()) -> 2
        locale.matches(GERMAN.toRegex()) -> 3
        locale.matches(SWEDISH.toRegex()) -> 4
        locale.matches(UKRAINIAN.toRegex()) -> 5
        locale.matches(RUSSIAN.toRegex()) -> 6
        else -> 0
    }


    companion object {
        const val ENGLISH = "en"
        const val FRENCH = "fr"
        const val GERMAN = "de"
        const val ITALIAN = "it"
        const val JAPANESE = "ja"
        const val KOREAN = "ko"
        const val POLISH = "pl"
        const val RUSSIAN = "ru"
        const val SPANISH = "es"
        const val UKRAINIAN = "uk"
        const val PORTUGUESE = "pt"
        const val SWEDISH = "sv"

        private const val EN = "en-US"
        private const val UK = "uk-UA"
        private const val ES = "es-ES"
        private const val PT = "pt-PT"
        private const val PL = "pl"
        private const val IT = "it"
        private const val SV = "sv"
        private const val FR = "fr"
        private const val DE = "de"
        private const val RU = "ru"

        fun getScreenLanguage(code: Int): Locale {
            return when (code) {
                0  -> Locale.ENGLISH
                1  -> Locale("es", "ES")
                2  -> Locale.FRENCH
                3  -> Locale.GERMAN
                4  -> Locale("sv", "")
                5  -> Locale("uk", "UA")
                6  -> Locale("ru", "")
                else -> Locale.getDefault()
            }
        }
    }
}
