package org.mifospay.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.os.bundleOf
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.common.Constants
import org.mifospay.feature.editpassword.EditPasswordActivity
import org.mifospay.feature.passcode.PassCodeActivity
import org.mifospay.theme.MifosTheme
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    @Inject
    lateinit var passcodePreferencesHelper: PasscodePreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                SettingsScreen(
                    backPress = { backToHome() },
                    onChangePassword = { onChangePasswordClicked() },
                    onChangePasscode = { onChangePasscodeClicked() }
                )
            }
        }
    }

    private fun backToHome() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun onChangePasswordClicked() {
        startActivity(Intent(applicationContext, EditPasswordActivity::class.java))
    }

    private fun onChangePasscodeClicked() {
        val currentPasscode = passcodePreferencesHelper.passCode
        passcodePreferencesHelper.savePassCode("")
        PassCodeActivity.startPassCodeActivity(
            context = this,
            bundle = bundleOf(
                Pair(Constants.CURRENT_PASSCODE, currentPasscode),
                Pair(Constants.UPDATE_PASSCODE, true)
            )
        )
    }

}