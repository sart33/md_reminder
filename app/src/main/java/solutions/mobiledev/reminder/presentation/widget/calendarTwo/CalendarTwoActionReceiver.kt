package solutions.mobiledev.reminder.presentation.widget.calendarTwo

import android.content.Context
import android.content.Intent
import solutions.mobiledev.reminder.core.Actions
import solutions.mobiledev.reminder.core.services.BaseBroadcast
import solutions.mobiledev.reminder.presentation.MainActivity

class CalendarTwoActionReceiver : BaseBroadcast() {


    override fun onReceive(context: Context, intent: Intent?) {

        if (intent != null) {
            val action = intent.action
            if (action != null && action.matches(Actions.Reminder.ACTION_EVENT_DATE.toRegex())) {
                val date = intent.getStringExtra(SELECTED_DATE)
                if (date != "") {
                    openLogged(
                        context,
                        Intent(context, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(SELECTED_DATE, date)
                    )
                }
            }
        }
    }


    private fun openLogged(context: Context, intent: Intent) {
        intent.putExtra(ARG_LOGGED, true)
        intent.action = Actions.Reminder.ACTION_EVENT_DATE
        context.startActivity(intent)
    }

    companion object {
        const val TYPE = "type"
        const val ARG_LOGGED = "arg_logged"
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val SELECTED_DATE = "selected_date"


    }
}

