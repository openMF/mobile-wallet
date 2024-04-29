package org.mifospay.receipt.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

@AndroidEntryPoint
class ReceiptActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                ReceiptScreen()
            }
        }
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