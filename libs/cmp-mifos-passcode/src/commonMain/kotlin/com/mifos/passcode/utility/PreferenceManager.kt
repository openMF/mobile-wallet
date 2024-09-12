package com.mifos.passcode.utility

import com.mifos.passcode.resources.Res
import com.mifos.passcode.resources.has_passcode
import com.mifos.passcode.resources.passcode
import com.russhwolf.settings.Settings

/**
 * @author pratyush
 * @since 15/3/24
 */

class PreferenceManager()
{
    private val settings : Settings by lazy {
        Settings()
    }

    var hasPasscode: Boolean
        get() = settings.getBoolean(Res.string.has_passcode.toString(), false)
        set(value) = settings.putBoolean(Res.string.has_passcode.toString(), value)

    fun savePasscode(passcode: String) {
        settings.putString(Res.string.passcode.toString(), passcode)
        hasPasscode = true
    }

    fun getSavedPasscode(): String {
        return settings.getString(Res.string.passcode.toString(), "")
    }

    fun clearPasscode() {
        settings.clear()
        hasPasscode = false
    }
}