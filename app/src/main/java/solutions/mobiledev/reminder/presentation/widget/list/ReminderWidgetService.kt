package solutions.mobiledev.reminder.presentation.widget.list


import android.content.Intent
import android.widget.RemoteViewsService
import org.koin.android.ext.android.get


class ReminderWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
         return ReminderListAdapter(intent, applicationContext, get(), get()
         )
    }


}


