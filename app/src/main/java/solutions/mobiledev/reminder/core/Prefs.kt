package solutions.mobiledev.reminder.core

import android.content.Context
import solutions.mobiledev.reminder.presentation.widget.PrefsConstants
import solutions.mobiledev.reminder.presentation.widget.SharedPrefs

class Prefs(context: Context) : SharedPrefs(context) {

    var formatDate: String
        get() = getString(PrefsConstants.DATE_FORMAT)
        set(value) = putString(PrefsConstants.DATE_FORMAT, value)

    var appLanguage: Int
        get() = getInt(PrefsConstants.APP_LANGUAGE, -1)
        set(value) = putInt(PrefsConstants.APP_LANGUAGE, value)


    var notificationBackgroundImage: Int
        get() = getInt(PrefsConstants.NOTIFICATION_BACKGROUND_IMAGE, 0)
        set(value) = putInt(PrefsConstants.NOTIFICATION_BACKGROUND_IMAGE, value)
    var notificationDefaultSound: String
        get() = getString(PrefsConstants.NOTIFICATION_DEFAULT_SOUND, "")
        set(value) = putString(PrefsConstants.NOTIFICATION_DEFAULT_SOUND, value)


    var ttsLocale: String
        get() = getString(PrefsConstants.TTS_LOCALE)
        set(value) = putString(PrefsConstants.TTS_LOCALE, value)


    var hourFormat: Int
        get() = getInt(PrefsConstants.TIME_FORMAT)
        set(value) = putInt(PrefsConstants.TIME_FORMAT, value)

    var homeScreenFull: Int
        get() = getInt(PrefsConstants.HOME_SCREEN_FULL, 1)
        set(value) = putInt(PrefsConstants.HOME_SCREEN_FULL, value)

    var lockScreenFull: Int
        get() = getInt(PrefsConstants.LOCK_SCREEN_FULL, 1)
        set(value) = putInt(PrefsConstants.LOCK_SCREEN_FULL, value)

    var themeChange: Int
        get() = getInt(PrefsConstants.LOCK_SCREEN_FULL, 1)
        set(value) = putInt(PrefsConstants.LOCK_SCREEN_FULL, value)


    var radius: Int
        get() = getInt(PrefsConstants.LOCATION_RADIUS)
        set(value) = putInt(PrefsConstants.LOCATION_RADIUS, value)


}