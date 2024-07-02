package org.mifospay.feature.read.qr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.mifospay.core.designsystem.theme.MifosTheme

/**
 * Created by naman on 7/9/17.
 */

class ReadQrActivity : ComponentActivity() {
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