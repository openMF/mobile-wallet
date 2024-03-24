package org.mifos.mobilewallet.mifospay.feature.passcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.mifos.mobile.passcode.MifosPassCodeActivity
import com.mifos.mobile.passcode.utils.EncryptionUtil
import com.mifos.mobile.passcode.utils.PassCodeConstants
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.common.Constants

@AndroidEntryPoint
class PassCodeActivity : MifosPassCodeActivity() {

    private var deepLinkURI: String? = null
    private var currPass: String? = ""
    private var updatePassword = false
    private var isInitialScreen = false

    val viewModel: PassCodeViewModel by viewModels()

    companion object {
        // We gonna remove it after implementing the Compose Passcode screen and compose navigation
        const val MAIN_ACTIVITY = "org.mifos.mobilewallet.mifospay.home.ui.MainActivity"
        const val LOGIN_ACTIVITY = "org.mifos.mobilewallet.mifospay.feature.auth.login.LoginActivity"
        const val RECEIPT_ACTIVITY = "org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity"

        fun startPassCodeActivity(context: Context, bundle: Bundle) {
            context.startActivity(Intent(context, PassCodeActivity::class.java).apply {
                putExtras(bundle)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isInitialScreen = intent.getBooleanExtra(
            PassCodeConstants.PASSCODE_INITIAL_LOGIN, false
        )
        if (intent != null) {
            currPass = intent.getStringExtra(Constants.CURRENT_PASSCODE)
            updatePassword = intent.getBooleanExtra(Constants.UPDATE_PASSCODE, false)
        }

        deepLinkURI = intent.getStringExtra("uri")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveCurrentPasscode()
                finishAffinity()
            }
        })
    }

    override fun getLogo(): Int {
        return 0
    }

    override fun startNextActivity() {
        // authenticate user with saved Preferences
        if (deepLinkURI != null) {
            val uri = Uri.parse(deepLinkURI)
            val intent = Intent(this@PassCodeActivity, Class.forName(RECEIPT_ACTIVITY))
            intent.data = uri
            startActivity(intent)
        } else {
            val intent = Intent(this@PassCodeActivity, Class.forName(MAIN_ACTIVITY))
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            startActivity(intent)
        }
    }

    override fun startLoginActivity() {
        AlertDialog.Builder(this@PassCodeActivity)
            .setTitle(R.string.feature_passcode_passcode_title)
            .setPositiveButton(R.string.feature_passcode_yes) { _, _ ->
                startActivity(Intent(this@PassCodeActivity, Class.forName(LOGIN_ACTIVITY)))
                finish()
            }.setNegativeButton(R.string.feature_passcode_cancel) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    override fun showToaster(view: View, msg: Int) {
        Toast.makeText(applicationContext, getText(msg), Toast.LENGTH_SHORT).show()
    }

    override fun getEncryptionType(): Int {
        return EncryptionUtil.DEFAULT
    }

    private fun saveCurrentPasscode() {
        if (updatePassword && currPass?.isNotEmpty() == true) {
            val helper = PasscodePreferencesHelper(this)
            helper.savePassCode(currPass)
        }
    }

    override fun skip(v: View) {
        saveCurrentPasscode()
        if (isInitialScreen) {
            startNextActivity()
        }
        finish()
    }
}
