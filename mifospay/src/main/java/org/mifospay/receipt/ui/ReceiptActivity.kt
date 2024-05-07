package org.mifospay.receipt.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.os.bundleOf
import com.mifos.mobile.passcode.utils.PassCodeConstants
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.feature.passcode.PassCodeActivity
import org.mifospay.theme.MifosTheme

@AndroidEntryPoint
class ReceiptActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                DownloadReceipt(
                    onShowSnackbar = { _, _ -> false },
                    openPassCodeActivity = { deepLinkURI ->
                        if (deepLinkURI != null) {
                            openPassCodeActivity(deepLinkURI)
                        }
                    }
                )
            }
        }
    }

    private fun openPassCodeActivity(deepLinkURI: Uri) {
        PassCodeActivity.startPassCodeActivity(
            context = this,
            bundle = bundleOf(
                Pair("uri", deepLinkURI.toString()),
                Pair(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
            ),
        )
        finish()
    }

    override fun onPause() {
        super.onPause()
        dismissProgressDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }
}