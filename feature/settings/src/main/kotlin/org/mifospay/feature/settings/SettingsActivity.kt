package org.mifospay.feature.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.editpassword.EditPasswordActivity
import org.mifospay.feature.passcode.PassCodeActivity
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

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