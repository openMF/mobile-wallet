package org.mifos.mobilewallet.mifospay.utils

import androidx.appcompat.app.AppCompatDelegate.*
import org.mifos.mobilewallet.mifospay.utils.Constants.*

/**
 * Created by Devansh on 20 July 2020
 */
object ThemeHelper {

    @JvmStatic
    fun applyTheme(applicationTheme: String) {
        when (applicationTheme) {

            LIGHT_THEME -> setDefaultNightMode(MODE_NIGHT_NO)

            DARK_THEME -> setDefaultNightMode(MODE_NIGHT_YES)

            else -> {
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) {
                    setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    // using light theme otherwise
                    setDefaultNightMode(MODE_NIGHT_NO)
                }
            }
        }
    }
}