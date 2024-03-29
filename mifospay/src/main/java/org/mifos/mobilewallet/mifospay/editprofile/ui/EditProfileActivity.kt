package org.mifos.mobilewallet.mifospay.editprofile.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.os.bundleOf
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.feature.passcode.PassCodeActivity
import org.mifos.mobilewallet.mifospay.password.ui.EditPasswordActivity
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    private var passcodePreferencesHelper: PasscodePreferencesHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passcodePreferencesHelper = PasscodePreferencesHelper(this)
        setContent {
            MifosTheme {
                EditProfileScreen(
                    onChangePassword = { onChangePasswordClicked() },
                    onChangePasscode = { onChangePasscodeClicked() },
                    onSaveChanges = {
                        // TODO : save locally or send it to backend
                    }
                )
            }
        }
    }

    private fun onChangePasswordClicked() {
        startActivity(Intent(applicationContext, EditPasswordActivity::class.java))
    }

    private fun onChangePasscodeClicked() {
        val currentPasscode = passcodePreferencesHelper?.passCode
        passcodePreferencesHelper?.savePassCode("")
        PassCodeActivity.startPassCodeActivity(
            context = this,
            bundle = bundleOf(
                Pair(Constants.CURRENT_PASSCODE, currentPasscode),
                Pair(Constants.UPDATE_PASSCODE, true)
            )
        )
    }
}