package solutions.mobiledev.reminder.presentation

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.presentation.notification.ReminderNotificationFragment


class StartNotificationFragmentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_notification_fragment)
        val fragmentName = intent.getStringExtra("fragment")
        if (fragmentName != null) {
            val remindId = intent.getStringExtra(REMINDER_ITEM_ID)
            val fragment = ReminderNotificationFragment()
            val bundle = Bundle()
            remindId?.let { bundle.putInt(REMINDER_ITEM_ID, it.toInt()) }
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
    @Suppress("DEPRECATION")
    private fun showWhenLockedAndTurnScreenOn() {
        if (prefs.lockScreenFull == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            } else {
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                )
            }
        }
    }

    companion object {

        const val REMINDER_ITEM_ID = "reminder_item_id"

    }
}