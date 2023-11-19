package solutions.mobiledev.reminder.presentation.widget.calendarTwo

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.core.Actions
import solutions.mobiledev.reminder.core.utils.WidgetUtils
import solutions.mobiledev.reminder.presentation.MainActivity
import solutions.mobiledev.reminder.presentation.widget.calendar.PendingIntentWrapper
import java.time.LocalDateTime
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class ReminderCalendarTwoWidget : AppWidgetProvider() {


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
            for (id in appWidgetIds) {
                updateWidget(context, appWidgetManager, CalendarTwoWidgetPrefsProvider(context, id))
            }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == "android.appwidget.action.APPWIDGET_UPDATE") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context!!, ReminderCalendarTwoWidget::class.java)
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.calendar_grid_view)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        updateWidget(context, appWidgetManager, CalendarTwoWidgetPrefsProvider(context, appWidgetId))
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }


    companion object {
        private const val OPEN_REMINDER_ADD_FRAGMENT = "open_reminder_add_fragment"
        private const val SELECTED_DATE = "selected_date"
        const val EVENT_DATE = "solutions.mobiledev.reminder.presentation.widget.calendarTwo.EVENT_DATE"


        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            sp: CalendarTwoWidgetPrefsProvider
        ) {
            val intent = Intent(context, ReminderCalendarTwoWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, sp.widgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val cal = GregorianCalendar()
            cal.timeInMillis = System.currentTimeMillis()

            val prefsProviderTwo = CalendarTwoWidgetPrefsProvider(context, sp.widgetId)
            val month = prefsProviderTwo.getMonth()
            val year = prefsProviderTwo.getYear()

            val headerBgColor = sp.getHeaderBackground()
            val bgColor = sp.getBackground()
            val views = RemoteViews(context.packageName, R.layout.reminder_calendar_two_widget)
            val line = if (WidgetUtils.isDarkBg(bgColor)) {
                R.drawable.line_drawable
            } else {
                R.drawable.line_black_drawable
            }
            if (WidgetUtils.isDarkBg(headerBgColor)) {
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_settings_24px, R.color.pureWhite,
                    R.id.btn_settings, CalendarTwoWidgetConfigActivity::class.java
                ) {
                    it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, sp.widgetId)
                    return@initButton it
                }
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_add_24px, R.color.pureWhite,
                    R.id.btn_add_task, MainActivity::class.java
                )  {
                    it.action = OPEN_REMINDER_ADD_FRAGMENT
                    return@initButton it
                }

                WidgetUtils.setIcon(
                    context,
                    views,
                    R.drawable.ic_twotone_keyboard_arrow_left_24px,
                    R.id.btn_prev,
                    R.color.pureWhite
                )
                WidgetUtils.setIcon(
                    context,
                    views,
                    R.drawable.ic_twotone_keyboard_arrow_right_24px,
                    R.id.btn_next,
                    R.color.pureWhite
                )

                views.setTextColor(
                    R.id.month_text_view,
                    ContextCompat.getColor(context, R.color.pureWhite)
                )
                views.setTextColor(
                    R.id.tvMonday,
                    ContextCompat.getColor(context, R.color.pureWhite)
                )
                views.setTextColor(
                    R.id.tvTuesday,
                    ContextCompat.getColor(context, R.color.pureWhite)
                )
                views.setTextColor(
                    R.id.tvWednesday,
                    ContextCompat.getColor(context, R.color.pureWhite)
                )
                views.setTextColor(
                    R.id.tvThursday,
                    ContextCompat.getColor(context, R.color.pureWhite)
                )
                views.setTextColor(
                    R.id.tvFriday,
                    ContextCompat.getColor(context, R.color.pureWhite)
                )
                views.setTextColor(
                    R.id.tvSaturday,
                    ContextCompat.getColor(context, R.color.pureWhite)

                )
                views.setInt(
                    R.id.line_image_view_two,
                    "setBackgroundColor",
                    Color.parseColor("#ffffff")
                )

            } else {
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_settings_24px, R.color.pureBlack,
                    R.id.btn_settings, CalendarTwoWidgetConfigActivity::class.java
                ) {
                    it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, sp.widgetId)
                    return@initButton it
                }
                WidgetUtils.initButton(
                    context, views, R.drawable.ic_twotone_add_24px, R.color.pureBlack,
                    R.id.btn_add_task, MainActivity::class.java
                )  {
                    it.action = OPEN_REMINDER_ADD_FRAGMENT
                    return@initButton it
                }

                WidgetUtils.setIcon(
                    context,
                    views,
                    R.drawable.ic_twotone_keyboard_arrow_left_24px,
                    R.id.btn_prev,
                    R.color.pureBlack
                )
                WidgetUtils.setIcon(
                    context,
                    views,
                    R.drawable.ic_twotone_keyboard_arrow_right_24px,
                    R.id.btn_next,
                    R.color.pureBlack
                )

                views.setTextColor(
                    R.id.month_text_view,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )
                views.setTextColor(
                    R.id.tvMonday,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )
                views.setTextColor(
                    R.id.tvTuesday,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )
                views.setTextColor(
                    R.id.tvWednesday,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )
                views.setTextColor(
                    R.id.tvThursday,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )
                views.setTextColor(
                    R.id.tvFriday,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )
                views.setTextColor(
                    R.id.tvSaturday,
                    ContextCompat.getColor(context, R.color.pureBlack)
                )
                views.setInt(
                    R.id.line_image_view_two,
                    "setBackgroundColor",
                    Color.parseColor("#000000")
                )

            }
            views.setInt(
                R.id.llHeaderBg,
                "setBackgroundResource",
                WidgetUtils.newWidgetBg(headerBgColor)
            )
            views.setInt(
                R.id.calendar_grid_view,
                "setBackgroundResource",
                WidgetUtils.newWidgetBg(bgColor)
            )
            views.setTextViewText( R.id.tvMonday, context.getString(R.string.monday))
            views.setTextViewText( R.id.tvTuesday, context.getString(R.string.tuesday))
            views.setTextViewText( R.id.tvWednesday, context.getString(R.string.wednesday))
            views.setTextViewText( R.id.tvThursday, context.getString(R.string.thursday))
            views.setTextViewText( R.id.tvFriday, context.getString(R.string.friday))
            views.setTextViewText( R.id.tvSaturday, context.getString(R.string.saturday))
            views.setTextViewText( R.id.tvSunday, context.getString(R.string.sunday))
            views.setTextViewText( R.id.tvSundayEnd, context.getString(R.string.sunday))
            views.setInt(R.id.line_image_view, "setBackgroundResource", line)

            if (prefsProviderTwo.getFirstDay() == 0) {
                views.setViewVisibility(R.id.tvSunday, View.VISIBLE)
                views.setViewVisibility(R.id.tvSundayEnd, View.GONE)

            }
            if (prefsProviderTwo.getFirstDay() == 1) {
                views.setViewVisibility(R.id.tvSunday, View.GONE)
                views.setViewVisibility(R.id.tvSundayEnd, View.VISIBLE)
            }
            views.setRemoteAdapter(R.id.calendar_grid_view, intent)
            when (month) {
                0 -> context.getString(R.string.january)
                1 -> context.getString(R.string.february)
                2 -> context.getString(R.string.march)
                3 -> context.getString(R.string.april)
                4 -> context.getString(R.string.may)
                5 -> context.getString(R.string.june)
                6 -> context.getString(R.string.july)
                7 -> context.getString(R.string.august)
                8 -> context.getString(R.string.september)
                9 -> context.getString(R.string.october)
                10 -> context.getString(R.string.november)
                11 -> context.getString(R.string.december)
                else -> {
                    "Error"
                }
            }.let {
                views.setTextViewText(R.id.month_text_view, "$it $year")
            }
            val startActivityIntent = Intent(context, CalendarTwoActionReceiver::class.java)
            startActivityIntent.action = Actions.Reminder.ACTION_EVENT_DATE
            val startActivityPendingIntent = PendingIntentWrapper.getBroadcast(
                context,
                0,
                startActivityIntent,
                PendingIntent.FLAG_MUTABLE,
                ignoreIn13 = true
            )
            views.setPendingIntentTemplate(R.id.calendar_grid_view, startActivityPendingIntent)



            val nextIntent = Intent(context, CalendarTwoNextReceiver::class.java)
            nextIntent.action = CalendarTwoNextReceiver.ACTION_NEXT
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, sp.widgetId)
            nextIntent.putExtra(CalendarTwoNextReceiver.ARG_VALUE, 2)
            val nextPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntentWrapper.getBroadcast(
                    context,
                    0,
                    nextIntent,
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                    ignoreIn13 = true
                )
            } else {
                PendingIntentWrapper.getBroadcast(
                    context,
                    0,
                    nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT,
                    ignoreIn13 = true
                )
            }
            views.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent)

            val previousIntent = Intent(context, CalendarTwoPreviousReceiver::class.java)
            previousIntent.action = CalendarTwoPreviousReceiver.ACTION_PREVIOUS
            previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, sp.widgetId)
            previousIntent.putExtra(CalendarTwoPreviousReceiver.ARG_VALUE, 1)
            val previousPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntentWrapper.getBroadcast(
                    context,
                    0,
                    previousIntent,
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                    ignoreIn13 = true
                )
            } else {
                PendingIntentWrapper.getBroadcast(
                    context,
                    0,
                    previousIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT,
                    ignoreIn13 = true
                )
            }
            // Запускаем previousPendingIntent при клике на R.id.btn_prev
            views.setOnClickPendingIntent(
                R.id.btn_prev, previousPendingIntent
            )
            appWidgetManager.updateAppWidget(sp.widgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(sp.widgetId, R.id.calendar_grid_view)
        }
    }
}
