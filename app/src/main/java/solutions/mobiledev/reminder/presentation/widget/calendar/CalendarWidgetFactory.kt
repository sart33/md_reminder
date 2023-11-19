package solutions.mobiledev.reminder.presentation.widget.calendar

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.LocaleList
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.Actions
import solutions.mobiledev.reminder.core.Prefs
import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.core.utils.WidgetUtils
import solutions.mobiledev.reminder.data.reminder.DateDiapason
import solutions.mobiledev.reminder.data.reminder.ReminderRepositoryImpl
import solutions.mobiledev.reminder.domain.entity.CalendarDate
import solutions.mobiledev.reminder.domain.reminder.GetRemindersCountAtDateDiapasonUseCase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class CalendarWidgetFactory(
    intent: Intent,
    private val context: Context,
    private val appWidgetManager: AppWidgetManager,
    private val appWidgetIds: IntArray,
    private val prefs: Prefs,
    private val language: Language

) : RemoteViewsService.RemoteViewsFactory {

    val application = context.applicationContext as Application
    private val remRepository = ReminderRepositoryImpl(application)
    val getRemindersCountAtDateDiapason = GetRemindersCountAtDateDiapasonUseCase(remRepository)
    private var data: List<DateDiapason> = emptyList()
    private var isDataReady = false
    private val latch = CountDownLatch(1)

    private var items = mutableListOf<CalendarDate>()

    private val widgetId: Int =
        intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
    private var mDay: Int = 0
    private var mMonth: Int = 0
    private var mYear: Int = 0
    private var firstDay: Int = -1
    private val prefsProvider = CalendarWidgetPrefsProvider(context, widgetId)

    private fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))

        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun enabledPrefLang(context: Context) {
        val init = prefs.appLanguage
        val language = if (prefs.appLanguage.toString().isNullOrEmpty()) {
            Locale.getDefault().language
        } else {
            val langName = language.getLanguage(init)
            langName.split("-")[0]
        }
        setLocale(context, language)
    }

    override fun onCreate() {
        val cal = GregorianCalendar()
        cal.timeInMillis = System.currentTimeMillis()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        prefsProvider.setMonth(month)
        prefsProvider.setYear(year)
        firstDay = prefsProvider.getFirstDay()
        enabledPrefLang(context)
    }


    override fun onDataSetChanged() {
        // Получаем список дат для отображения в виджете
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val prefsMonth = prefsProvider.getMonth()
        val year = prefsProvider.getYear()
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
        firstDay = prefsProvider.getFirstDay()
        items = getDatesForMonth(prefsMonth, year)
        enabledPrefLang(context)
    }


    override fun onDestroy() {}

    override fun getCount(): Int {
        if (!isDataReady) {
            try {
                latch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return items.size
    }


    override fun getViewAt(position: Int): RemoteViews {
        // Создаем новый виджет и настраиваем его

        val views = RemoteViews(context.packageName, R.layout.widget_item)
        val bgColor = prefsProvider.getBackground()
        val textColor = if (WidgetUtils.isDarkBg(bgColor)) {
            ContextCompat.getColor(context, R.color.pureWhite)
        } else {
            ContextCompat.getColor(context, R.color.pureBlack)
        }
        val todayColor = if (WidgetUtils.isDarkBg(bgColor)) {
            ContextCompat.getColor(context, R.color.pureBlack)
        } else {
            ContextCompat.getColor(context, R.color.pureWhite)
        }
        views.setTextViewText(R.id.tv_date, items[position].date)

        // применить цвет текста к R.id.tv_date
        if (items[position].eventCount > 0) {

            views.setTextViewText(
                R.id.tv_count,
                items[position].eventCount.toString()
            )
            // применить цвет фона к R.id.tv_count
            views.setInt(R.id.tv_count, "setBackgroundResource", R.drawable.calendar_widget_rem)


        } else {
            views.setTextViewText(R.id.tv_count, "")
            views.setInt(R.id.tv_count, "setBackgroundColor", Color.parseColor("#00000000"))
        }
        if (items[position].current) {
            views.setInt(
                R.id.widgetItem,
                "setBackgroundColor",
                Color.parseColor("#E8F84D")
            )
            views.setTextColor(
                R.id.tv_date,
                todayColor
            )
        } else {
            views.setInt(
                R.id.widgetItem,
                "setBackgroundColor",
                Color.parseColor("#00000000")

            )
            views.setTextColor(
                R.id.tv_date,
                textColor
            )
        }

        if (items[position].dayOff) views.setTextColor(
            R.id.tv_date,
            Color.parseColor("#CE1508")
        )

        val prefsMonth = prefsProvider.getMonth()
        val year = prefsProvider.getYear()
        var dayStr = "00"
        val monthStr = if (prefsMonth < 9) {
            "0${prefsMonth + 1}"
        } else {
            "${prefsMonth + 1}"
        }
        dayStr = if (items[position].date != "") {

            if (items[position].date.toInt() < 10) {
                "0${items[position].date}"
            } else {
                items[position].date
            }
        } else {
            "00"
        }
        // Обрабатываем нажатие на виджет
        val date = "$year-$monthStr-$dayStr"


        val fillInIntent = Intent()
        fillInIntent.action = Actions.Reminder.ACTION_EDIT_REMIND
        fillInIntent.putExtra(SELECTED_DATE, date)
        views.setOnClickFillInIntent(R.id.widgetItem, fillInIntent)
        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    // Метод для получения количества дней в месяце
    private fun getDaysInMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Метод для получения дня недели, на котором начинается месяц
    private fun getFirstDayOfMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    //     Метод для получения списка дат для указанного месяца и года
    private fun getDatesForMonth(month: Int, year: Int): MutableList<CalendarDate> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStr = if (month < 9) {
            "0${month + 1}"
        } else {
            "${month + 1}"
        }
        //Получить последний день текущего месяца
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        // Получаем текущую дату
        val currentDate = Date()

        // Форматируем дату для получения текущего месяца и года
        val dayFormatter = SimpleDateFormat("dd", Locale.getDefault())
        val monthFormatter = SimpleDateFormat("MM", Locale.getDefault())
        val dayString = dayFormatter.format(currentDate)
        val monthString = monthFormatter.format(currentDate)

        // Создаем массив для дней месяца и заполняем его
        val daysOfMonth = mutableListOf<CalendarDate>()
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            data = withContext(Dispatchers.IO) {
                return@withContext getRemindersCountAtDateDiapason(
                    "$year-$monthStr-01",
                    "$year-$monthStr-$lastDayOfMonth"
                )
            }
            setData(data)
            isDataReady = true
            latch.countDown()


        }
        if (!isDataReady) {
            try {
                latch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        val daysInMonth = getDaysInMonth(month, year)
        val firstDayOfMonth = getFirstDayOfMonth(month, year)
        for (i in 1..daysInMonth) {
            if (monthString == monthStr && i == dayString.toInt()) {
                daysOfMonth.add(i - 1, CalendarDate(i, i.toString(), false, monthStr, true, 0))
            } else {
                daysOfMonth.add(i - 1, CalendarDate(i, i.toString(), false, monthStr, false, 0))
            }
        }

        if (firstDay == 0 || firstDay == 1) {
            if (firstDay == 0) {
                // Добавляем пустые ячейки перед первым днем месяца
                for (i in 1 until firstDayOfMonth) {
                    daysOfMonth.add(0, CalendarDate(0, "", false, "", false, 0))
                }
                // Добавляем пустые ячейки после последнего дня месяца
                while (daysOfMonth.size % 7 != 0) {
                    daysOfMonth.add(
                        firstDayOfMonth + daysInMonth - 1,
                        CalendarDate(0, "", false, "", false, 0)
                    )
                }
                // Каждое воскресенье отметить красным цветом
                for (i in 0 until daysOfMonth.size) {
                    if (i % 7 == 0) {
                        daysOfMonth[i].dayOff = true
                    }
                }
            } else {
                // Добавляем пустые ячейки перед первым днем месяца
                for (i in 1 until firstDayOfMonth - 1) {
                    daysOfMonth.add(0, CalendarDate(0, "", false, "", false, 0))
                }
                // Добавляем пустые ячейки после последнего дня месяца
                while (daysOfMonth.size % 7 != 0) {
                    daysOfMonth.add(
                        firstDayOfMonth + daysInMonth - 2,
                        CalendarDate(0, "", false, "", false, 0)
                    )
                }

                // Каждое воскресенье отметить красным цветом
                for (i in 0 until daysOfMonth.size) {
                    if (i % 7 == 6) {
                        daysOfMonth[i].dayOff = true
                    }
                }
            }
        }

        // Добавление пустых ячеек в конец списка для получениыя общего количесва ячеек в количестве 42
        for (i in 1..(42 - daysOfMonth.size)) {
            daysOfMonth.add(CalendarDate(0, "", false, "", false, 0))
        }


        data.forEach {
            val date = it.date_remaining
            var day = date.substring(8, 10)
            val dayN = day.substring(0, 1)
            if (dayN == "0") day = day.substring(1,2)
            daysOfMonth.forEach { dayOfMonth ->
                if (dayOfMonth.date == day) {
                    dayOfMonth.eventCount = it.count.toInt()
                }
            }
        }

        return daysOfMonth

    }


    private fun setData(data: List<DateDiapason>) {
        this.data = data


    }

    companion object {

        private const val SELECTED_DATE = "selected_date"

    }
}