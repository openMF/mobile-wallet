package org.mifospay.feature.auth.chooseAuthOption

import com.russhwolf.settings.Settings
import org.mifospay.feature.auth.chooseAuthOption.utils.Helpers

const val APP_LOCK_KEY = "auth_method"
const val REGISTRATION_DATA = "REGISTRATION_DATA"

class ChooseAuthOptionRepository(
    private val settings: Settings,
) {

    fun setAuthOption(option: AppLockOption) {
        settings.putString(
            APP_LOCK_KEY,
            Helpers.authOptionToStringMapperFunction(option),
        )
    }

    fun getAuthOption(): AppLockOption {
        return Helpers.stringToAuthOptionMapperFunction(
            settings.getString(
                APP_LOCK_KEY,
                "",
            ),
        )
    }

    fun clearAuthOption() {
        settings.remove(APP_LOCK_KEY)
    }

    fun saveRegistrationData(registrationData: String) {
        settings.putString(REGISTRATION_DATA, registrationData)
    }

    fun getRegistrationData() = settings.getString(REGISTRATION_DATA, "")

    fun clearRegistrationData() {
        settings.remove(REGISTRATION_DATA)
    }
}
