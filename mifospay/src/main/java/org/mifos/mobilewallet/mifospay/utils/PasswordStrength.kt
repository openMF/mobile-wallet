package org.mifos.mobilewallet.mifospay.utils

import android.graphics.Color
import android.util.Log
import org.mifos.mobilewallet.mifospay.R

class PasswordStrength(password: String) {
    var strengthStringId = 0
    var colorResId = 0
    private var currentScore = 0
    var value = 0

    init {
        Log.e("log", "INIT : " + currentScore + " " + password.length)
        currentScore = if (password.length < 6) {
            0
        } else if (password.length < 12) {
            1
        } else {
            2
        }
        if (password.length > 6) {
            for (i in 0 until password.length) {
                val c = password[i]
                if (Character.isUpperCase(c)) {
                    currentScore++
                    break
                }
            }
        }
        if (password.length > 6) {
            for (i in 0 until password.length) {
                val c = password[i]
                if (Character.isDigit(c)) {
                    currentScore++
                    break
                }
            }
        }
        Log.e("log", "Final : $currentScore")
        when (currentScore) {
            0 -> {
                value = 0
                strengthStringId = R.string.password_very_weak
                colorResId = -1
            }

            1 -> {
                value = 25
                strengthStringId = R.string.password_weak
                colorResId = Color.RED
            }

            2 -> {
                value = 50
                strengthStringId = R.string.password_normal
                colorResId = Color.YELLOW
            }

            3 -> {
                value = 75
                strengthStringId = R.string.password_strong
                colorResId = Color.GREEN
            }

            4 -> {
                value = 100
                strengthStringId = R.string.password_very_strong
                colorResId = Color.BLUE
            }

            else -> {
                value = -1
                strengthStringId = R.string.password_very_weak
                colorResId = -1
            }
        }
    }
}
