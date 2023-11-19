package solutions.mobiledev.reminder.presentation.widget

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.io.*

abstract class SharedPrefs(protected val context : Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PrefsConstants.PREFS_NAME, Context.MODE_PRIVATE)

    fun putString(stringToSave: String, value: String) {
        prefs.edit().putString(stringToSave, value).apply()
    }

    fun putInt(stringToSave: String, value: Int) {
        prefs.edit().putInt(stringToSave, value).apply()
    }

    fun getInt(stringToLoad: String, def: Int = 0): Int {
        return try {
            prefs.getInt(stringToLoad, def)
        } catch (e: ClassCastException) {
            try {
                Integer.parseInt(prefs.getString(stringToLoad, "$def") ?: "$def")
            } catch (e1: ClassCastException) {
                def
            }
        }
    }


    fun putObject(key: String, obj: Any) {
        putString(key, Gson().toJson(obj))
    }

    fun getString(stringToLoad: String, def: String = ""): String {
        return prefs.getString(stringToLoad, def) ?: def
    }

    fun hasKey(checkString: String): Boolean {
        return prefs.contains(checkString)
    }

    fun removeKey(checkString: String) {
        prefs.edit().remove(checkString).apply()
    }

    fun putBoolean(stringToSave: String, value: Boolean) {
        prefs.edit().putBoolean(stringToSave, value).apply()
    }

    fun getBoolean(stringToLoad: String, def: Boolean = false): Boolean {
        return try {
            prefs.getBoolean(stringToLoad, def)
        } catch (e: ClassCastException) {
            java.lang.Boolean.parseBoolean(prefs.getString(stringToLoad, "false"))
        }
    }

    fun saveVersionBoolean(stringToSave: String) {
        prefs.edit().putBoolean(stringToSave, true).apply()
    }

    fun getVersion(stringToLoad: String): Boolean {
        return try {
            prefs.getBoolean(stringToLoad, false)
        } catch (e: ClassCastException) {
            java.lang.Boolean.parseBoolean(prefs.getString(stringToLoad, "false"))
        }
    }

    fun all(): Map<String, *> {
        return prefs.all
    }

    fun sharedPrefs(): SharedPreferences {
        return prefs
    }


}