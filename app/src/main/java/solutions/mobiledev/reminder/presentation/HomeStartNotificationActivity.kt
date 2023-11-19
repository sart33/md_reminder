package solutions.mobiledev.reminder.presentation

import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.presentation.notification.ReminderNotificationFragment

class HomeStartNotificationActivity : BaseActivity() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.activity_start_notification_fragment, null)
        setContentView(R.layout.activity_start_notification_fragment)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER or Gravity.START
        params.x = 0
        params.y = 0

        windowManager.addView(floatingView, params)
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


    companion object {
        const val REMINDER_ITEM_ID = "reminder_item_id"

    }
}
