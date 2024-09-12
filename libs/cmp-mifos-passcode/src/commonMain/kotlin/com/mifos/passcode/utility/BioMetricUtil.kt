package com.mifos.passcode.utility

interface BioMetricUtil {

    suspend fun setAndReturnPublicKey(): String?
    suspend fun authenticate(): AuthenticationResult
    fun canAuthenticate(): Boolean
    fun generatePublicKey(): String?
    fun signUserId(ucc: String): String
    fun isBiometricSet(): Boolean
    fun getPublicKey(): String?
    fun isValidCrypto(): Boolean
}

sealed class AuthenticationResult {
    data object Success: AuthenticationResult()
    data object Failed: AuthenticationResult()
    data object AttemptExhausted: AuthenticationResult()
    data object NegativeButtonClick: AuthenticationResult()
    data class Error(val error: String): AuthenticationResult()
}