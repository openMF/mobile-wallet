package org.mifospay.qr.showQr.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.common.Constants
import org.mifospay.qr.showQr.presenter.ShowQrViewModel
import org.mifospay.theme.MifosTheme
import javax.inject.Inject

/**
 * Created by naman on 8/7/17.
 */
@AndroidEntryPoint
class ShowQrActivity : BaseActivity() {

    private val viewModel: ShowQrViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setQrData(intent.getStringExtra(Constants.QR_DATA))
        setContent {
            MifosTheme {
                ShowQrScreen(
                    backPress = { onBackPressed() }
                )
            }
        }
    }
}