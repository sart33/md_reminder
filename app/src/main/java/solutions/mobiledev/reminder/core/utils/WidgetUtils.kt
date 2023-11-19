package solutions.mobiledev.reminder.core.utils

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.presentation.widget.calendar.PendingIntentWrapper

object WidgetUtils {

    fun initButton(context: Context, rv: RemoteViews, @DrawableRes iconId: Int, @ColorRes color: Int,
                   @IdRes viewId: Int, cls: Class<*>, extras: ((Intent) -> Intent)? = null) {
        var configIntent = Intent(context, cls)
        if (extras != null) {
            configIntent = extras.invoke(configIntent)
        }
        extras?.invoke(configIntent)
        val configPendingIntent = PendingIntentWrapper.getActivity(context, 0, configIntent, 0)
        rv.setOnClickPendingIntent(viewId, configPendingIntent)
        setIcon(context, rv, iconId, viewId, color)
    }

    fun initButton(context: Context, rv: RemoteViews, @DrawableRes iconId: Int, @ColorRes color: Int,
                   @IdRes viewId: Int, cls: Class<*>) {
        val configIntent = Intent(context, cls)
        val configPendingIntent = PendingIntentWrapper.getActivity(context, 0, configIntent, 0)
        rv.setOnClickPendingIntent(viewId, configPendingIntent)
        setIcon(context, rv, iconId, viewId, color)
    }

    fun setIcon(context: Context, rv: RemoteViews, @DrawableRes iconId: Int, @IdRes viewId: Int, @ColorRes color: Int) {
        rv.setImageViewBitmap(viewId, ViewUtils.createIcon(context, iconId, ContextCompat.getColor(context, color)))
    }

    fun setIcon(rv: RemoteViews, @DrawableRes iconId: Int, @IdRes viewId: Int) {
        rv.setImageViewResource(viewId, iconId)
    }

    @DrawableRes
    fun newWidgetBg(code: Int): Int {
        return when (code) {

            0 -> R.drawable.widget_bg_white
            1 -> R.drawable.widget_bg_white_90
            2 -> R.drawable.widget_bg_white_80
            3 -> R.drawable.widget_bg_white_70
            4 -> R.drawable.widget_bg_white_60
            5 -> R.drawable.widget_bg_white_50
            6 -> R.drawable.widget_bg_white_40
            7 -> R.drawable.widget_bg_white_30
            8 -> R.drawable.widget_bg_white_20
            9 -> R.drawable.widget_bg_white_10
            10 -> R.drawable.widget_bg_transparent
            11 -> R.drawable.widget_bg_transparent
            12 -> R.drawable.widget_bg_black_10
            13 -> R.drawable.widget_bg_black_20
            14 -> R.drawable.widget_bg_black_30
            15 -> R.drawable.widget_bg_black_40
            16 -> R.drawable.widget_bg_black_50
            17 -> R.drawable.widget_bg_black_60
            18 -> R.drawable.widget_bg_black_70
            19 -> R.drawable.widget_bg_black_80
            20-> R.drawable.widget_bg_black_90
            21 -> R.drawable.widget_bg_black
            else -> R.drawable.widget_bg_black
        }
    }

    fun isDarkBg(code: Int): Boolean = code > 10
}


