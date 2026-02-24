package org.mifospay.core.data.repositoryImpl

import com.russhwolf.settings.Settings
import org.mifospay.core.data.repository.PlatformAuthenticationDataRepository

const val BIOMETRIC_REGISTRATION_KEY = "org.mifos.mifospay.biometric.registration.key"

class PlatformAuthenticationDataRepositoryImpl(
    private val settings: Settings
): PlatformAuthenticationDataRepository {
    override fun saveBiometricRegistrationData(registrationData: String) {
        settings.putString(BIOMETRIC_REGISTRATION_KEY, registrationData)
    }

    override fun getBiometricRegistrationData(): String {
        return settings.getString(BIOMETRIC_REGISTRATION_KEY, "")
    }

    override fun clearBiometricRegistrationData() {
        settings.remove(BIOMETRIC_REGISTRATION_KEY)
    }
}