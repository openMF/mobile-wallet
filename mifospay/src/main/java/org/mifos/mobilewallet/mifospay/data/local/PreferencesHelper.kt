package org.mifos.mobilewallet.mifospay.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by naman on 17/6/17.
 */
@Singleton
class PreferencesHelper @Inject constructor(@ApplicationContext context: Context?) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun getInt(preferenceKey: String?, preferenceDefaultValue: Int): Int {
        return sharedPreferences.getInt(preferenceKey, preferenceDefaultValue)
    }

    fun putInt(preferenceKey: String?, preferenceValue: Int) {
        sharedPreferences.edit().putInt(preferenceKey, preferenceValue).apply()
    }

    fun getLong(preferenceKey: String?, preferenceDefaultValue: Long): Long {
        return sharedPreferences.getLong(preferenceKey, preferenceDefaultValue)
    }

    fun putLong(preferenceKey: String?, preferenceValue: Long) {
        sharedPreferences.edit().putLong(preferenceKey, preferenceValue).apply()
    }

    fun getString(preferenceKey: String?, preferenceDefaultValue: String?): String? {
        return sharedPreferences.getString(preferenceKey, preferenceDefaultValue)
    }

    fun putString(preferenceKey: String?, preferenceValue: String?) {
        sharedPreferences.edit().putString(preferenceKey, preferenceValue).apply()
    }

    fun saveToken(token: String?) {
        putString(TOKEN, token)
    }

    fun clearToken() {
        putString(TOKEN, "")
    }

    val token: String?
        get() = getString(TOKEN, "")

    fun saveFullName(name: String?) {
        putString(NAME, name)
    }

    val fullName: String?
        get() = getString(NAME, "")

    fun saveUsername(name: String?) {
        putString(USERNAME, name)
    }

    val username: String?
        get() = getString(USERNAME, "")

    fun saveEmail(email: String?) {
        putString(EMAIL, email)
    }

    val email: String?
        get() = getString(EMAIL, "")

    fun saveMobile(mobile: String?) {
        putString(MOBILE_NO, mobile)
    }

    val mobile: String?
        get() = getString(MOBILE_NO, "")
    var userId: Long
        get() = getLong(USER_ID, -1)
        set(id) {
            putLong(USER_ID, id)
        }
    var clientId: Long
        get() = getLong(CLIENT_ID, 1)
        set(clientId) {
            putLong(CLIENT_ID, clientId)
        }
    var clientVpa: String?
        get() = getString(CLIENT_VPA, "")
        set(vpa) {
            putString(CLIENT_VPA, vpa)
        }
    var accountId: Long
        get() = getLong(ACCOUNT_ID, 0)
        set(accountId) {
            putLong(ACCOUNT_ID, accountId)
        }
    var firebaseRegId: String?
        get() = getString(FIREBASE_REG_ID, "")
        set(firebaseRegId) {
            putString(FIREBASE_REG_ID, firebaseRegId)
        }

    companion object {
        private const val TOKEN = "preferences_token"
        private const val NAME = "preferences_name"
        private const val USERNAME = "preferences_user_name"
        private const val EMAIL = "preferences_email"
        private const val CLIENT_ID = "preferences_client"
        private const val USER_ID = "preferences_user_id"
        private const val CLIENT_VPA = "preferences_client_vpa"
        private const val MOBILE_NO = "preferences_mobile_no"
        private const val FIREBASE_REG_ID = "preferences_firebase_reg_id"
        private const val ACCOUNT_ID = "preferences_account_id"
    }
}