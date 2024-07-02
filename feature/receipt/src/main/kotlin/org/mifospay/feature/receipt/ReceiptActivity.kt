package org.mifospay.feature.receipt

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.mifos.mobile.passcode.utils.PassCodeConstants
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.passcode.PassCodeActivity

@AndroidEntryPoint
class ReceiptActivity : AppCompatActivity() {

    private val receiptViewModel: ReceiptViewModel by viewModels()
    private var deepLinkURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.data
        deepLinkURI = data
        receiptViewModel.getTransactionData(data)

        setContent {
            MifosTheme {
                ReceiptScreenRoute(
                    onShowSnackbar = { _, _ -> false },
                    openPassCodeActivity = { openPassCodeActivity() },
                    onBackClick = { finish() }
                )
            }
        }
    }

    private fun openPassCodeActivity() {
        PassCodeActivity.startPassCodeActivity(
            context = this,
            bundle = bundleOf(
                Pair("uri", deepLinkURI.toString()),
                Pair(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
            ),
        )
        finish()
    }

}