package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate

/**
 * @author mmikhailov on 27.02.2020.
 */
class PrefManager(context: Context) {
    internal val preferences: SharedPreferences by lazy { PreferenceManager(context).sharedPreferences }

    var storedBoolean by PrefDelegate(false)
    var storedString by PrefDelegate("")
    var storedInt by PrefDelegate(1)
    var storedLong by PrefDelegate(0L)
    var storedFloat by PrefDelegate(0f)

    fun clearAll() {
        preferences.edit().clear().apply()
    }
}