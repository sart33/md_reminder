package solutions.mobiledev.reminder.presentation.widget.list

import android.content.Context
import solutions.mobiledev.reminder.presentation.widget.calendar.WidgetPrefsProvider

class ReminderListPrefsProvider(
    context: Context,
    internal val widgetId: Int

) : WidgetPrefsProvider(context, "new_list_pref", widgetId) {


    fun setWidgetId(value: Int) {
        putInt(WIDGET_NAME, value)
    }

    fun getWidgetId(): Int {
        return getInt(WIDGET_NAME, 0)
    }
    fun setTitleDateFormat(value: String) {
        putString(TITLE_FORMAT_OF_DATE, value)
    }
    fun getTitleDateFormat(): String {
        return getString(TITLE_FORMAT_OF_DATE, "yyyy-MM-dd")
    }
    fun setDateFormat(value: String) {
        putString(FORMAT_OF_DATE, value)
    }
    fun getDateFormat(): String {
        return getString(FORMAT_OF_DATE, "yyyy-MM-dd")
    }
    fun setHeaderBackground(value: Int) {
        putInt(WIDGET_HEADER_BG_COLOR, value)
    }

    fun getHeaderBackground(): Int {
        return getInt(WIDGET_HEADER_BG_COLOR, 0)
    }

    fun setItemBackground(value: Int) {
        putInt(WIDGET_ITEM_BG, value)
    }

    fun getItemBackground(): Int {
        return getInt(WIDGET_ITEM_BG)
    }

    fun setTextSize(value: Float) {
        putFloat(WIDGET_TEXT_SIZE, value)
    }

    fun getTextSize(): Float {
        return getFloat(WIDGET_TEXT_SIZE)
    }

    companion object {
        private const val FORMAT_OF_DATE = "format_of_date"
        private const val TITLE_FORMAT_OF_DATE = "title_format_of_date"
        private const val WIDGET_HEADER_BG_COLOR = "new_list_header_bg"
        private const val WIDGET_ITEM_BG = "new_list_item_bg"
        private const val WIDGET_TEXT_SIZE = "new_list_text_size"
        private const val WIDGET_NAME = "new_list_pref"

    }
}
