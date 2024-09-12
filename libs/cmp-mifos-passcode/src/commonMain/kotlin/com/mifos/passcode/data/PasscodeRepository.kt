package com.mifos.passcode.data

interface PasscodeRepository {
    fun getSavedPasscode(): String
    val hasPasscode: Boolean
    fun savePasscode(passcode: String)
    fun clearPasscode()
}