package org.mifospay.qr.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

/**
 * Created by naman on 7/9/17.
 */

class ReadQrActivity : BaseActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MifosTheme {
                ShowQrScreenRoute(
                    backPress = { onBackPressed() }
                )
            }
        }
    }
}