package solutions.mobiledev.reminder.presentation.settings

import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.TextProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeManager(
    private val prefs: Prefs,
    private val textProvider: TextProvider,
    private val language: Language
) {

    private fun localizedDateFormatter(pattern: String): DateTimeFormatter {
        return DateTimeFormatter.ofPattern(pattern, Language.getScreenLanguage(prefs.appLanguage))
    }

    fun getTime(time: LocalTime): String {
        return if (prefs.hourFormat == 0) {
            time.format(time24Formatter())
        } else {
            time.format(time12Formatter())
        }
    }

    fun getDate(date: LocalDate): String? {
        when (prefs.formatDate) {
            "dd.MM.yyyy" -> date.format(dateFormatterOne())
            "dd/MM/yyyy" -> date.format(dateFormatterTwo())
            "dd-MM-yyyy" -> date.format(dateFormatterThree())
            "yyyy.MM.dd" -> date.format(dateFormatterFour())
            "yyyy/MM/dd" -> date.format(dateFormatterFive())
            "yyyy-MM-dd" -> date.format(dateFormatterSix())
            "MM.dd.yyyy" -> date.format(dateFormatterSeven())
            "MM/dd/yyyy" -> date.format(dateFormatterEight())
            "MM-dd-yyyy" -> date.format(dateFormatterNine())
            "yyyy MMMM dd" -> date.format(dateFormatterTen())
            "yyyy dd MMMM" -> date.format(dateFormatterEleven())
            "dd MMMM yyyy" -> date.format(dateFormatterTwelve())
            "MMMM dd yyyy" -> date.format(dateFormatterThirteen())
            else -> date.format(dateFormatterOne())
        }.let {
            return it
        }
    }




    private fun time24Formatter(): DateTimeFormatter = localizedDateFormatter("HH:mm")

    private fun time12Formatter(): DateTimeFormatter = localizedDateFormatter("h:mm a")


    private fun dateFormatterOne(): DateTimeFormatter = localizedDateFormatter("dd.MM.yyyy")
    private fun dateFormatterTwo(): DateTimeFormatter = localizedDateFormatter("dd/MM/yyyy")
    private fun dateFormatterThree(): DateTimeFormatter = localizedDateFormatter("dd-MM-yyyy")
    private fun dateFormatterFour(): DateTimeFormatter = localizedDateFormatter("yyyy.MM.dd")
    private fun dateFormatterFive(): DateTimeFormatter = localizedDateFormatter("yyyy/MM/dd")
    private fun dateFormatterSix(): DateTimeFormatter = localizedDateFormatter("yyyy-MM-dd")
    private fun dateFormatterSeven(): DateTimeFormatter = localizedDateFormatter("MM.dd.yyyy")
    private fun dateFormatterEight(): DateTimeFormatter = localizedDateFormatter("MM/dd/yyyy")
    private fun dateFormatterNine(): DateTimeFormatter = localizedDateFormatter("MM-dd-yyyy")

    private fun dateFormatterTen(): DateTimeFormatter = localizedDateFormatter("yyyy MMMM dd")
    private fun dateFormatterEleven(): DateTimeFormatter = localizedDateFormatter("yyyy dd MMMM")
    private fun dateFormatterTwelve(): DateTimeFormatter = localizedDateFormatter("dd MMMM yyyy")
    private fun dateFormatterThirteen(): DateTimeFormatter = localizedDateFormatter("MMMM dd yyyy")

}