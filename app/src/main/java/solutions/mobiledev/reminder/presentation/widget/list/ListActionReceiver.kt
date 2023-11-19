package solutions.mobiledev.reminder.presentation.widget.list

import android.content.Context
import android.content.Intent
import solutions.mobiledev.reminder.core.Actions
import solutions.mobiledev.reminder.core.services.BaseBroadcast
import solutions.mobiledev.reminder.presentation.MainActivity
import solutions.mobiledev.reminder.presentation.widget.Constants

class ListActionReceiver : BaseBroadcast() {


    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action != null && action.matches(Actions.Reminder.ACTION_EDIT_REMIND.toRegex())) {
                val id = intent.getIntExtra(Constants.INTENT_ID, 0)
                val isReminder = intent.getBooleanExtra(TYPE, true)
                if (id != 0) {
                    if (isReminder) {
                        openLogged(
                            context,
                            Intent(context, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Constants.INTENT_ID, id)
                        )
                    }
                }
            }
        }
    }


    private fun openLogged(context: Context, intent: Intent) {
        intent.putExtra(ARG_LOGGED, true)
        intent.action = Actions.Reminder.ACTION_EDIT_REMIND
        context.startActivity(intent)
    }

    companion object {
        const val TYPE = "type"
        const val ARG_LOGGED = "arg_logged"
        const val REMINDER_ITEM_ID = "reminder_item_id"


    }
}