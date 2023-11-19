package solutions.mobiledev.reminder.presentation.widget.list

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.utils.ViewUtils
import solutions.mobiledev.reminder.core.utils.WidgetUtils
import solutions.mobiledev.reminder.databinding.ActivityListWidgetConfigBinding
import solutions.mobiledev.reminder.domain.entity.SettingItem
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.widget.BindingActivity
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


class ListWidgetConfigActivity : BindingActivity<ActivityListWidgetConfigBinding>() {

    private lateinit var format: SettingItem
    private var dateFormat = "dd MMMM yyyy"
    private lateinit var titleDateFormat: String
    private lateinit var viewModel: ReminderViewModel
    private lateinit var curDate1: String
    private lateinit var curDate2: String
    private lateinit var curDate3: String
    private lateinit var curDate4: String

    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var resultValue: Intent? = null
    private var textSize: Int = 14
    private lateinit var prefsProvider: ReminderListPrefsProvider


    override fun inflateBinding() = ActivityListWidgetConfigBinding.inflate(layoutInflater)

    private fun enabledPrefLang() {
        val init = prefs.appLanguage
        val language = if (prefs.appLanguage == -1) {
            val currentLocale: Locale = Locale.getDefault()
            currentLocale.language
        } else {
            val langName = language.getLanguage(init)
            langName.split("-")[0]
        }
        viewModel.setLocale(this, language)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        readIntent()
        binding.fabSave.setOnClickListener {
//            showTextSizeDialog()
            savePrefs()
        }
        binding.ivToolbar.setOnClickListener { finish() }

        binding.bgColorSlider.setSelectorColorResource(
//            if (isDarkMode) R.color.pureWhite else
            R.color.pureBlack
        )
        binding.bgColorSlider.setListener { position, _ ->
            binding.llHeaderBg.setBackgroundResource(WidgetUtils.newWidgetBg(position))
            updateIcons(position)
        }

        binding.listItemBgColorSlider.setSelectorColorResource(
//            if (isDarkMode) R.color.pureWhite else
            R.color.pureBlack
        )
        binding.listItemBgColorSlider.setListener { position, _ ->
            binding.llWidgetBg.setBackgroundResource(WidgetUtils.newWidgetBg(position))
            updateText(position)
        }
        val localTime = LocalTime.now().minusHours(3).minusMinutes(15)
        binding.tvWidgetTime.text = dateTimeManager.getTime(localTime)
        val localDate = LocalDate.now().plusMonths(2).plusDays(14)
        binding.tvWidgetDate.text = dateTimeManager.getDate(localDate)
        if (!prefs.formatDate.isNullOrBlank()) dateFormat = prefs.formatDate
        binding.tvWidgetMessage.text = getString(R.string.pick_up_things_from_dry_cleaners)
        titleDateFormat = "EE, $dateFormat"
        curDate1 = viewModel.currentDateFormat(titleDateFormat)
        binding.rbDateFormatOne.text = curDate1
        titleDateFormat = "EEEE, $dateFormat"
        curDate2 = viewModel.currentDateFormat(titleDateFormat)
        binding.rbDateFormatTwo.text = curDate2
        titleDateFormat = "$dateFormat, EE"
        curDate3 = viewModel.currentDateFormat(titleDateFormat)
        binding.rbDateFormatThree.text = curDate3
        titleDateFormat = "$dateFormat, EEEE"
        curDate4 = viewModel.currentDateFormat(titleDateFormat)
        binding.rbDateFormatFour.text = curDate4
        binding.rbDateFormatOne.setOnClickListener {
            updateDateFormat(curDate1)
            titleDateFormat = "EE, $dateFormat"
        }
        binding.rbDateFormatTwo.setOnClickListener {
            updateDateFormat(curDate2)
            titleDateFormat = "EEEE, $dateFormat"
        }
        binding.rbDateFormatThree.setOnClickListener {
            updateDateFormat(curDate3)
            titleDateFormat = "$dateFormat, EE"
        }
        binding.rbDateFormatFour.setOnClickListener {
            updateDateFormat(curDate4)
            titleDateFormat = "$dateFormat, EEEE"
        }

        updateText(0)
        updateIcons(0)
        updateDateFormat(viewModel.currentDateFormat(titleDateFormat))
        enabledPrefLang()
        showCurrentTheme()
    }


    private fun showCurrentTheme() {
        textSize = prefsProvider.getTextSize().takeIf { it != 0f }?.toInt() ?: 14
        val headerBg = prefsProvider.getHeaderBackground()
        binding.bgColorSlider.setSelection(headerBg)
        binding.llHeaderBg.setBackgroundResource(WidgetUtils.newWidgetBg(headerBg))
        updateIcons(headerBg)
        val itemBg = prefsProvider.getItemBackground()
        binding.listItemBgColorSlider.setSelection(itemBg)
        binding.llWidgetBg.setBackgroundResource(WidgetUtils.newWidgetBg(itemBg))
        updateText(itemBg)
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

        prefsProvider = ReminderListPrefsProvider(this, widgetID)
        resultValue = Intent()
        resultValue?.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        setResult(RESULT_CANCELED, resultValue)
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
    }

    private fun updateText(code: Int) {
        val isDark = WidgetUtils.isDarkBg(code)
        if (isDark) {

            binding.tvWidgetDate.setTextColor(ContextCompat.getColor(this, R.color.pureWhite))
            binding.tvWidgetTime.setTextColor(ContextCompat.getColor(this, R.color.pureWhite))
            binding.tvWidgetMessage.setTextColor(ContextCompat.getColor(this, R.color.pureWhite))
            binding.lineImageViewTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.pureWhite))



            binding.ivWidgetMailIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_mail
                )
            )
            binding.ivWidgetPhoneIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_phone
                )
            )
            binding.ivWidgetAttachFileIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_attachfile
                )
            )
            binding.ivWidgetSmsIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_sms
                )
            )

        } else {

            binding.tvWidgetDate.setTextColor(ContextCompat.getColor(this, R.color.pureBlack))
            binding.tvWidgetTime.setTextColor(ContextCompat.getColor(this, R.color.pureBlack))
            binding.tvWidgetMessage.setTextColor(ContextCompat.getColor(this, R.color.pureBlack))
            binding.lineImageViewTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.pureBlack))
            binding.ivWidgetMailIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_mail_black
                )
            )
            binding.ivWidgetPhoneIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_phone_black
                )
            )
            binding.ivWidgetAttachFileIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_attachfile_black
                )
            )
            binding.ivWidgetSmsIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.widget_sms_black
                )
            )
        }
    }

    private fun updateIcons(code: Int) {

        val isDark = WidgetUtils.isDarkBg(code)
        binding.btnSettings.setImageDrawable(
            ViewUtils.tintIcon(
                this,
                R.drawable.ic_twotone_settings_24px,
                isDark
            )
        )
        binding.btnAddTask.setImageDrawable(
            ViewUtils.tintIcon(
                this,
                R.drawable.ic_twotone_add_24px,
                isDark
            )
        )

        if (isDark) {
            binding.widgetTitle.setTextColor(ContextCompat.getColor(this, R.color.pureWhite))
        } else {
            binding.widgetTitle.setTextColor(ContextCompat.getColor(this, R.color.pureBlack))
        }
    }


    private fun updateDateFormat(currentDate: String) {
        binding.widgetTitle.text = currentDate
    }


    private fun setFormat(format: SettingItem) {
        this.format = format
    }


    private fun savePrefs() {
        prefsProvider.setHeaderBackground(binding.bgColorSlider.selectedItem)
        prefsProvider.setItemBackground(binding.listItemBgColorSlider.selectedItem)
        prefsProvider.setDateFormat(dateFormat)
        prefsProvider.setTitleDateFormat(titleDateFormat)
//        prefsProvider.setTextSize(textSize.toFloat())

        val appWidgetManager = AppWidgetManager.getInstance(this)
        ReminderListWidget.updateWidget(this, appWidgetManager, prefsProvider)
        setResult(RESULT_OK, resultValue)
        finish()
    }

}