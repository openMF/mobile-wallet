package org.mifospay.shared.core.data

import com.google.gson.Gson
import org.mifospay.shared.modal.domain.Client
import org.mifospay.shared.modal.domain.User
import java.util.prefs.Preferences

actual class PreferencesHelper{
    private val sharedPreferences = Preferences.userRoot().node(this.javaClass.name)

    fun clear() {
        sharedPreferences.clear()
    }

    fun getInt(preferenceKey: String?, preferenceDefaultValue: Int): Int {
        return sharedPreferences.getInt(preferenceKey, preferenceDefaultValue)
    }

    fun putInt(preferenceKey: String?, preferenceValue: Int) {
        sharedPreferences.putInt(preferenceKey, preferenceValue)
    }

    fun getLong(preferenceKey: String?, preferenceDefaultValue: Long): Long {
        return sharedPreferences.getLong(preferenceKey, preferenceDefaultValue)
    }

    fun putLong(preferenceKey: String?, preferenceValue: Long) {
        sharedPreferences.putLong(preferenceKey, preferenceValue)
    }

    fun getString(preferenceKey: String?, preferenceDefaultValue: String?): String? {
        return sharedPreferences.get(preferenceKey, preferenceDefaultValue)
    }

    fun putString(preferenceKey: String?, preferenceValue: String?) {
        sharedPreferences.put(preferenceKey, preferenceValue)
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
        get() = getString(NAME, " ")

    fun saveUsername(name: String?) {
        putString(USERNAME, name)
    }

    val username: String
        get() = getString(USERNAME, "") ?: ""

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

    var user: User
        get() = Gson().fromJson(
            getString(PREF_USER, Gson().toJson(User::class.java)),
            User::class.java,
        )
        set(user) {
            putString(PREF_USER, Gson().toJson(user))
        }

    var client: Client
        get() = Gson().fromJson(
            getString(PREF_CLIENT, Gson().toJson(Client::class.java)),
            Client::class.java,
        )
        set(client) {
            putString(PREF_USER, Gson().toJson(client))
        }

    companion object {
        private const val PREF_USER = "pref_user"
        private const val PREF_CLIENT = "pref_client"
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