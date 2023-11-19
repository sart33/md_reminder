package solutions.mobiledev.reminder.presentation.widget.list

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.utils.WidgetUtils
import solutions.mobiledev.reminder.presentation.MainActivity
import solutions.mobiledev.reminder.presentation.widget.calendar.PendingIntentWrapper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Implementation of App Widget functionality.
 */


class ReminderListWidget : AppWidgetProvider() {


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, ReminderListPrefsProvider(context, id))
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }


    companion object {

        fun updateWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            prefsProvider: ReminderListPrefsProvider
        ) {
            val cal = GregorianCalendar()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.calendar = cal
            val time = timeFormat.format(cal.time)
            val headerBgColor = prefsProvider.getHeaderBackground()
            val itemBgColor = prefsProvider.getItemBackground()
            val titleFormatOfDate = prefsProvider.getTitleDateFormat()
            val date = LocalDate.now()
            val titleFormatter = DateTimeFormatter.ofPattern(titleFormatOfDate)
            val titleFormattedDate = date.format(titleFormatter)
            val views = RemoteViews(context.packageName, R.layout.reminder_list_widget)
            views.setTextViewText(R.id.tvWidgetTitleDate, titleFormattedDate)
            views.setTextViewText(R.id.tvWidgetTime, time)


            if (WidgetUtils.isDarkBg(headerBgColor)) {
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_settings_24px, R.color.pureWhite,
                    R.id.ivWidgetSetting, ListWidgetConfigActivity::class.java
                ) {
                    it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, prefsProvider.widgetId)
                    return@initButton it
                }
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_add_24px, R.color.pureWhite,
                    R.id.ivWidgetPlus, MainActivity::class.java
                ) {
                    it.action = OPEN_REMINDER_ADD_FRAGMENT
                    return@initButton it
                }
                views.setInt(
                    R.id.line_image_view_two,
                    "setBackgroundResource",
                    WidgetUtils.newWidgetBg(0)
                )

                views.setTextColor(
                    R.id.tvWidgetTitleDate,
                    ContextCompat.getColor(context, R.color.pureWhite)
                )
            } else {
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_settings_24px, R.color.pureBlack,
                    R.id.ivWidgetSetting, ListWidgetConfigActivity::class.java
                ) {
                    it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, prefsProvider.widgetId)
                    return@initButton it
                }
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_add_24px, R.color.pureBlack,
                    R.id.ivWidgetPlus, MainActivity::class.java
                ) {
                    it.action = OPEN_REMINDER_ADD_FRAGMENT
                    return@initButton it
                }
                views.setInt(
                    R.id.line_image_view_two,
                    "setBackgroundResource",
                    WidgetUtils.newWidgetBg(21)
                )
                views.setTextColor(
                    R.id.tvWidgetTitleDate,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )

            }
            views.setInt(
                R.id.llHeaderBg,
                "setBackgroundResource",
                WidgetUtils.newWidgetBg(headerBgColor)
            )
            views.setInt(
                R.id.llWidgetBg,
                "setBackgroundResource",
                WidgetUtils.newWidgetBg(itemBgColor)
            )
            val startActivityIntent = Intent(context, ListActionReceiver::class.java)
            val startActivityPendingIntent = PendingIntentWrapper.getBroadcast(
                context,
                0,
                startActivityIntent,
                PendingIntent.FLAG_MUTABLE,
                ignoreIn13 = true
            )

            views.setPendingIntentTemplate(R.id.lvWidget, startActivityPendingIntent)
            val adapter = Intent(context, ReminderWidgetService::class.java)
            adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, prefsProvider.widgetId)
            views.setRemoteAdapter(R.id.lvWidget, adapter)
            appWidgetManager.updateAppWidget(prefsProvider.widgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(prefsProvider.widgetId, R.id.lvWidget)

        }

        lateinit var appWidgetManager: AppWidgetManager
        private const val OPEN_REMINDER_ADD_FRAGMENT = "open_reminder_add_fragment"

    }
}



