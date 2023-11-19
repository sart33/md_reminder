package solutions.mobiledev.reminder.presentation.widget.calendarTwo

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.utils.ViewUtils
import solutions.mobiledev.reminder.core.utils.WidgetUtils
import solutions.mobiledev.reminder.databinding.ActivityCalendarTwoWidgetConfigBinding
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.widget.BindingActivity
import java.util.*

class CalendarTwoWidgetConfigActivity : BindingActivity<ActivityCalendarTwoWidgetConfigBinding>() {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var resultValue: Intent? = null
    private var firstDay = -1
    private lateinit var prefsProviderTwo: CalendarTwoWidgetPrefsProvider
    private lateinit var viewModel: ReminderViewModel


    override fun inflateBinding() = ActivityCalendarTwoWidgetConfigBinding.inflate(layoutInflater)

    private fun enabledPrefLang() {
        val init = prefs.appLanguage
        val language = if (prefs.appLanguage == -1) {
            val currentLocale: Locale = Locale.getDefault()
            currentLocale.language

            // Если значение языка не сохранено, используется язык устройства по умолчанию

        } else {
            val langName = language.getLanguage(init)
            langName.split("-")[0]

        }
        viewModel.setLocale(this, language)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        val cal = GregorianCalendar()
        cal.timeInMillis = System.currentTimeMillis()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        when (month) {
            0 -> getString(R.string.january)
            1 -> getString(R.string.february)
            2 -> getString(R.string.march)
            3 -> getString(R.string.april)
            4 -> getString(R.string.may)
            5 -> getString(R.string.june)
            6 -> getString(R.string.july)
            7 -> getString(R.string.august)
            8 -> getString(R.string.september)
            9 -> getString(R.string.october)
            10 -> getString(R.string.november)
            11 -> getString(R.string.december)
            else -> {
                "Error"
            }
        }.let {
            binding.widgetTitle.text = "$it $year"
        }
        readIntent()
        binding.fabSave.setOnClickListener { savePrefs() }
        binding.ivToolbar.setOnClickListener { finish() }
        binding.bgColorSlider.setSelectorColorResource(
//        if (isDarkMode) R.color.pureWhite else
            R.color.pureBlack
        )
        binding.bgColorSlider.setListener { position, _ ->
            updateContent(position)
        }
        binding.headerBgColorSlider.setSelectorColorResource(
//            if (isDarkMode) R.color.pureWhite else
            R.color.pureBlack
        )
        binding.headerBgColorSlider.setListener { position, _ ->
            binding.llHeaderBg.setBackgroundResource(WidgetUtils.newWidgetBg(position))
            binding.llSubHeaderBg.setBackgroundResource(WidgetUtils.newWidgetBg(position))
            updateHeader(position)
            updateSubHeader(position)
        }

        enabledPrefLang()
        updateContent(0)
        updateHeader(0)
        updateSubHeader(0)
        showCurrentTheme()
        getFirstDay0fWeek()

        binding.rbMondayTwo.setOnClickListener {
            binding.tvSunday.visibility = View.GONE
            binding.tvSundayEnd.visibility = View.VISIBLE
            firstDay = 1
            prefsProviderTwo.setFirstDay(firstDay)

        }
        binding.rbSundayTwo.setOnClickListener {
            binding.tvSunday.visibility = View.VISIBLE
            binding.tvSundayEnd.visibility = View.GONE
            firstDay = 0
            prefsProviderTwo.setFirstDay(firstDay)

        }

    }


    private fun getFirstDay0fWeek() {
        firstDay = prefsProviderTwo.getFirstDay()


        when {
            (firstDay == 0) -> {
                binding.rbSundayTwo.isChecked = true
                binding.tvSundayEnd.visibility = View.GONE
                binding.tvSunday.visibility = View.VISIBLE
            }
            (firstDay == 1) -> {
                binding.rbMondayTwo.isChecked = true
                binding.tvSundayEnd.visibility = View.VISIBLE
                binding.tvSunday.visibility = View.GONE


            }
            else -> {
                binding.rbSundayTwo.isChecked = true
                binding.tvSundayEnd.visibility = View.VISIBLE
                binding.tvSunday.visibility = View.GONE
            }
        }
    }


    private fun showCurrentTheme() {
        val headerBg = prefsProviderTwo.getHeaderBackground()
        binding.headerBgColorSlider.setSelection(headerBg)
        binding.llHeaderBg.setBackgroundResource(WidgetUtils.newWidgetBg(headerBg))
        binding.llSubHeaderBg.setBackgroundResource(WidgetUtils.newWidgetBg(headerBg))
        updateHeader(headerBg)
        updateSubHeader(headerBg)

        val itemBg = prefsProviderTwo.getBackground()
        binding.bgColorSlider.setSelection(itemBg)
        updateContent(itemBg)
    }


    private fun updateContent(code: Int) {
        binding.llWidgetBg.setBackgroundResource(WidgetUtils.newWidgetBg(code))
    }

    private fun updateSubHeader(code: Int) {
        val color = if (WidgetUtils.isDarkBg(code)) {
            ContextCompat.getColor(this, R.color.pureWhite)
        } else {
            ContextCompat.getColor(this, R.color.pureBlack)
        }
        val colorRed = ContextCompat.getColor(
            this,
            R.color.widget_calendar_red_color
        )
        binding.tvMonday.setTextColor(color)
        binding.tvTuesday.setTextColor(color)
        binding.tvWednesday.setTextColor(color)
        binding.tvThursday.setTextColor(color)
        binding.tvFriday.setTextColor(color)
        binding.tvSaturday.setTextColor(color)
        binding.tvSundayEnd.setTextColor(colorRed)
        binding.tvSunday.setTextColor(colorRed)
        binding.lineImageView.setBackgroundColor(color)

    }

    private fun updateHeader(code: Int) {
        val color = if (WidgetUtils.isDarkBg(code)) {
            ContextCompat.getColor(this, R.color.pureWhite)
        } else {
            ContextCompat.getColor(this, R.color.pureBlack)
        }

        binding.btnSettings.setImageBitmap(
            ViewUtils.createIcon(
                this,
                R.drawable.ic_twotone_settings_24px,
                color
            )
        )
        binding.btnAddTask.setImageBitmap(
            ViewUtils.createIcon(
                this,
                R.drawable.ic_twotone_add_24px,
                color
            )
        )
        binding.btnNext.setImageBitmap(
            ViewUtils.createIcon(
                this,
                R.drawable.ic_twotone_keyboard_arrow_right_24px,
                color
            )
        )
        binding.btnPrev.setImageBitmap(
            ViewUtils.createIcon(
                this,
                R.drawable.ic_twotone_keyboard_arrow_left_24px,
                color
            )
        )
        binding.widgetTitle.setTextColor(color)

    }


    private fun readIntent() {
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        prefsProviderTwo = CalendarTwoWidgetPrefsProvider(this, widgetID)
        resultValue = Intent()
        resultValue?.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        setResult(RESULT_CANCELED, resultValue)
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
    }


    private fun savePrefs() {
        val cal = GregorianCalendar()
        cal.timeInMillis = System.currentTimeMillis()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        prefsProviderTwo.setBackground(binding.bgColorSlider.selectedItem)
        prefsProviderTwo.setHeaderBackground(binding.headerBgColorSlider.selectedItem)
        prefsProviderTwo.setMonth(month)
        prefsProviderTwo.setYear(year)
        prefsProviderTwo.setFirstDay(firstDay)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        ReminderCalendarTwoWidget.updateWidget(this, appWidgetManager, prefsProviderTwo)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    companion object {
        private const val CALENDAR_PERMISSION_REQUEST_CODE = 126

    }

}
