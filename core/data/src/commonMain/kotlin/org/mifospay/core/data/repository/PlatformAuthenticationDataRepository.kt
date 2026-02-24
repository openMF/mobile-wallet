package org.mifospay.core.data.repository

interface PlatformAuthenticationDataRepository {

    fun saveBiometricRegistrationData(registrationData: String)

    fun getBiometricRegistrationData(): String

    fun clearBiometricRegistrationData()

}