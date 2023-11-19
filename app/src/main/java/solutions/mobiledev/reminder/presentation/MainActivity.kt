package solutions.mobiledev.reminder.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.Actions
import solutions.mobiledev.reminder.databinding.ActivityMainBinding
import solutions.mobiledev.reminder.presentation.notification.ReminderItemNotificationFragment
import solutions.mobiledev.reminder.presentation.notification.ReminderNotificationFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderItemFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListAtDate
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment
import solutions.mobiledev.reminder.presentation.widget.Constants
import java.util.*


class MainActivity() : BaseActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ReminderViewModel

    private fun enabledPrefLang() {
        /***
         * init language
         */
//        prefs.appLanguage = -1
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
        /**
         * end of init language
         */
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SartReminder_Dark)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
            viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
//            recreate()
            enabledPrefLang()
            updatesHelper.updateWidgets()
            if (intent?.action == Actions.Reminder.ACTION_EDIT_REMIND) {
                updatesHelper.updateWidgets()
                val id = intent.getIntExtra(Constants.INTENT_ID, 0)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, ReminderItemNotificationFragment.newInstance(id))
                    .addToBackStack(ReminderItemNotificationFragment.NAME)
                    .commit()
            } else if (intent?.action == OPEN_REMINDER_ADD_FRAGMENT) {
                updatesHelper.updateWidgets()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, ReminderAddFragment.newInstance())
                    .addToBackStack(ReminderAddFragment.NAME)
                    .commit()
            } else if (intent?.action == Actions.Reminder.ACTION_EVENT_DATE) {
                updatesHelper.updateWidgets()
                val date = intent.getStringExtra(SELECTED_DATE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, ReminderListAtDate.newInstance(date))
                    .addToBackStack(ReminderListAtDate.NAME)
                    .commit()

            } else {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, StartAppFragment.newInstance())
                        .add(R.id.main_container, StartAppFragment.newInstance())
                        .commit()
                }
            }
//        }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.hasExtra("notification_extra") == true) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, ReminderListFragment.newInstance())
                .addToBackStack(ReminderListFragment.NAME)
                .commit()
        }
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
        super.onBackPressed()
    }



    companion object {
        private const val CHANNEL_ID = "channel_id"
        const val SCREEN_MODE = "extra_mode"
        const val MODE_ADD = "mode_add"
        private const val OPEN_REMINDERS_LIST_OF_DATE_FRAGMENT = "reminders_list_of_date_fragment"
        private const val DATE = "date"
        const val SELECTED_DATE = "selected_date"
        const val REMINDER_ITEM_ID = "reminder_item_id"
        private const val OPEN_REMINDER_ADD_FRAGMENT = "open_reminder_add_fragment"
        private const val ACTION_DIAPASON_OF_DATES = "solutions.mobiledev.reminder.presentation.widget.calendar.DIAPASON_OF_DATES"
        private const val FRAGMENT = "fragment"


        fun newInstance() = StartAppFragment()
    }
}



