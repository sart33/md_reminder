package solutions.mobiledev.reminder.presentation.widget.calendar

import android.content.Context

abstract class WidgetPrefsProvider(
    context: Context,
    fileName: String,
    private val widgetId: Int
) {

    private val sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun putFloat(key: String, value: Float) {
        sp.edit().putFloat(key + widgetId, value).apply()
    }

    fun getFloat(key: String, def: Float = 0f): Float {
        return sp.getFloat(key + widgetId, def)
    }

    fun putInt(key: String, value: Int) {
        sp.edit().putInt(key + widgetId, value).apply()
    }

    fun getInt(key: String, def: Int = 0): Int {
        return sp.getInt(key + widgetId, def)
    }

    fun putString(key: String, value: String) {
        sp.edit().putString(key + widgetId, value).apply()
    }

    fun getString(key: String, def: String = "yyyy-MM-dd"): String {
        return sp.getString(key + widgetId, def).toString()

    }
}