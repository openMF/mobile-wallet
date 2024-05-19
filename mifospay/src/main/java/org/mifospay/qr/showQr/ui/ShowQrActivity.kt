package org.mifospay.qr.showQr.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.common.Constants
import org.mifospay.theme.MifosTheme

/**
 * Created by naman on 8/7/17.
 */
@AndroidEntryPoint
class ShowQrActivity : BaseActivity() {

    private lateinit var qrDataString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrDataString = intent.getStringExtra(Constants.QR_DATA) ?: ""
        setContent {
            MifosTheme {
                ShowQrScreen(
                    qrDataString = qrDataString,
                    backPress = { onBackPressed() }
                )
            }
        }
    }
}